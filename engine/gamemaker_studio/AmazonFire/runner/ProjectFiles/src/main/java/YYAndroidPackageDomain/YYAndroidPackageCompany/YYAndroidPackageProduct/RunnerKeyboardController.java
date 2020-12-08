package ${YYAndroidPackageName};

import android.os.Bundle;
import android.os.Build;
import android.os.Debug;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.view.View;
import android.view.LayoutInflater;
import android.view.Display;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.InputDevice;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.graphics.Rect;
import android.graphics.Color;
import android.content.Context;
import android.app.Activity;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.lang.String;

import com.yoyogames.runner.RunnerJNILib;

public class RunnerKeyboardController
{
	// Keyboard status
	public static final String KEYBOARD_STATUS_SHOWING = "showing";
	public static final String KEYBOARD_STATUS_VISIBLE = "visible";
	public static final String KEYBOARD_STATUS_HIDING =  "hiding";
	public static final String KEYBOARD_STATUS_HIDDEN =  "hidden";

	// Default keyboard event SOURCE_KEYBOARD
	public static final int KEYBOARD_EVENT_SOURCE_DEFAULT = InputDevice.SOURCE_TOUCHSCREEN | InputDevice.SOURCE_KEYBOARD;

	// DEBUG: Show the hidden text field used for reading input
	public static final boolean DEBUG_SHOW_HIDDEN_TEXT_FIELD = false;

	// Handles keyboard event results, keeps a reference to the keyboard controller that owns it
	private class KeyboardResultReceiver extends ResultReceiver
	{
		protected RunnerKeyboardController m_keyboardController = null;

		KeyboardResultReceiver(RunnerKeyboardController controller)
		{
			super(null);
			m_keyboardController = controller;
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) 
		{
			
		}
	};

	// Handles layout resize events and checks if the keyboard was hidden/shown
	private class KeyboardLayoutListener implements OnGlobalLayoutListener
	{
		protected RunnerKeyboardController m_keyboardController = null;

		KeyboardLayoutListener(RunnerKeyboardController controller)
		{
			m_keyboardController = controller;
		}

		@Override
		public void onGlobalLayout() 
		{
	       m_keyboardController.OnActivityLayoutChanged();
		}
	};

	// Handles key deletion
	private class KeyboardInputConnectionWrapper extends InputConnectionWrapper
	{
		protected RunnerKeyboardController m_keyboardController = null;
		protected InputConnection m_inputConnection = null;

		public KeyboardInputConnectionWrapper(InputConnection target, boolean mutable, RunnerKeyboardController controller) 
		{
            super(target, mutable);
			m_keyboardController = controller;
			m_inputConnection = target;
        }

		@Override
		public void setTarget(InputConnection target)
		{
			//Log.i("yoyo", "[VK] Setting input connection target to: " + target);			

			super.setTarget(target);
			m_inputConnection = target;
		}

		private boolean handleBackspaceEvent()
		{
			if(m_currentPredictiveTextEnabled == false)
			{
				RunnerJNILib.KeyEvent(0, KeyEvent.KEYCODE_DEL, 0, KEYBOARD_EVENT_SOURCE_DEFAULT);
				RunnerJNILib.KeyEvent(1, KeyEvent.KEYCODE_DEL, 0, KEYBOARD_EVENT_SOURCE_DEFAULT);
			}
			else
			{
				String currentText = m_editText.getText().toString();
				m_keyboardController.SetInputString(currentText.substring(0, Math.max(0, currentText.length() - 1)), false);
			}
								
			return false;
		}

		@Override
		public boolean sendKeyEvent(KeyEvent event) 
		{
			//Log.i("yoyo", "[VK] SendKeyEvent. Key: " + event.getKeyCode());
			if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) 
			{
				//Log.i("yoyo", "[VK] Backspace key detected via sendKeyEvent");
				return handleBackspaceEvent();
			}
								
			return super.sendKeyEvent(event);
		}
							
		@Override
		public boolean deleteSurroundingText(int beforeLength, int afterLength) 
		{
			// magic: in latest Android, deleteSurroundingText(1, 0) will be called for backspace
			if (beforeLength == 1 && afterLength == 0) 
			{
				//Log.i("yoyo", "[VK] Backspace detected via deleteSurroundingText");
				return handleBackspaceEvent();
			}

			return super.deleteSurroundingText(beforeLength, afterLength);
		}

		@Override
		public boolean setComposingRegion (int start, int end)
		{
			//Log.i("yoyo", "[VK] SETCOMPOSINGREGION IN INPUT CONNECTION. " + start + " to: "+ end );

			int newStartingIndex = start;
			if(newStartingIndex > 0)
			{
				String currentText = "";
				ExtractedText currentExtractedText = getExtractedText(new ExtractedTextRequest(), 0);
				if(currentExtractedText != null && currentExtractedText.text != null)
					currentText = currentExtractedText.text.toString();

				//Log.i("yoyo", "[VK] SETCOMPOSINGREGION CURRENT TEXT: " + currentText);
				
				int currentTextLength = currentText.length();
				if(currentTextLength > 0)
				{
					//Log.i("yoyo", "[VK] CHECKING FOR INDEX MISMATCH IN COMPOSING REGION Text: " + currentText + ". Str length: " + currentTextLength);
					newStartingIndex = Math.min(newStartingIndex, currentTextLength - 1);

					// BUGFIX: The starting index should always be whitespace in the main text string, check if that is the case
					while(newStartingIndex > 0)
					{
						//Log.i("yoyo", "[VK] Checking character index: " + (newStartingIndex - 1));

						char currentChar = currentText.charAt(newStartingIndex - 1);
						String currentCharAsStr = Character.toString(currentChar);
						//Log.i("yoyo", "[VK] Checking character: " + currentCharAsStr);
					
						// Iterate towards the start of the string until we find some whitespace
						if(Character.isWhitespace(currentChar) == true || Pattern.matches("\\p{Punct}", currentCharAsStr) == true)
						{
							//Log.i("yoyo", "Found whitespace. Skipping..");
							break;
						}

						--newStartingIndex;
					}
				}
			}

			//Log.i("yoyo", "[VK] Returning adjusted starting index: "+newStartingIndex+"\n");
			return super.setComposingRegion(newStartingIndex, end);
		}
	};

	// Hidden edit text field that handles reading the input
	private class KeyboardInputEditText extends EditText
	{
		protected RunnerKeyboardController m_keyboardController = null;

		public KeyboardInputEditText(Context context, RunnerKeyboardController controller) 
		{
			super(context);
			m_keyboardController = controller;
		}

		@Override
		public boolean onKeyDown( int keyCode, KeyEvent event ) 
		{
			return false;
		}

		@Override
		public boolean onKeyUp( int keyCode, KeyEvent event ) 
		{
			return false;
		}

		@Override
		public boolean onKeyPreIme(int keyCode, KeyEvent event) 
		{
			//Log.i("yoyo", "[VK] onKeyPreIme. Key code: " + keyCode + ". Event source: "+event.getSource() + ". Event action: "+event.getAction());

			// Mark the virtual keyboard as hidden when the back key is pressed
			// On Gamepads, do the same for the B key
			if (event.getAction() == KeyEvent.ACTION_UP && ((keyCode == KeyEvent.KEYCODE_BACK) || 
				(keyCode == KeyEvent.KEYCODE_BUTTON_B && (event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD))) 
			{
				//Log.i("yoyo", "[VK] Back key detected, hiding keyboard");
				VirtualKeyboardHide();
			}

			return false;
		}

		@Override
		protected void onSelectionChanged(int selStart, int selEnd) 
		{
			// Set selection to end of string always (to mirror GMS keyboard functions)
			//Log.i("yoyo", "[VK] Overriding selection change ( "+selStart+", "+selEnd+" ) - moving cursor to end of string.");
			int strLen = getText().length();
			setSelection(strLen);
		}

		@Override
		public boolean onCheckIsTextEditor()
		{
			// BUGFIX: 0029338: Android: Virtual Keyboard: Crash when opening ascii-type and autocap-none keyboard
			// This was causing a crash when the input method flags were equal to 0.
			// For some reason the default method would return false on this being a text field at that point.
			return true;
		}

		@Override
		public boolean isSuggestionsEnabled ()
		{
			if(m_keyboardController != null)
				return m_keyboardController.GetPredictiveTextEnabled();

			return super.isSuggestionsEnabled();
		}
				
		@Override
		public InputConnection onCreateInputConnection(EditorInfo attrs)
		{
			//Log.i("yoyo", "[VK] Setting up new IC wrapper. Input type: " + attrs.inputType + ". IME: " + attrs.imeOptions);
			
			InputConnection connection = super.onCreateInputConnection(attrs);
			InputConnection ic = new KeyboardInputConnectionWrapper(connection, true, m_keyboardController);
			return ic;
		}
	};
	
	///////////////////////////////////////////////////////////////////
	// MEMBERS
	///////////////////////////////////////////////////////////////////

	private Context m_context = null;
	private InputMethodManager m_inputMethodManager = null;	
	private View m_activityView = null;
	private Handler m_viewHandler = null;

	private String m_keyboardStatus = KEYBOARD_STATUS_HIDDEN;			// Status of the keyboard
	private boolean m_virtualKeyboardActive = false;					// True if the keyboard is active (determined via InputMethodManager soft input status)
	private boolean m_virtualKeyboardVisible = false;					// True if the keyboard is visible (estimated via layout changes, may be incorrect depending on user keyboard settings)
	private boolean m_virtualKeyboardStatusRequested = false;
	private boolean m_physicalKeyboardConnected = false;
	
	private KeyboardResultReceiver m_virtualKeyboardToggleResultReceiver = null;
	private KeyboardResultReceiver m_virtualKeyboardVisibilityCheckAdjustReceiver = null;
	private KeyboardResultReceiver m_virtualKeyboardVisibilityCheckResultReceiver = null;

	private EditText m_editText = null;

	private Rect m_viewActiveRect = new Rect();
	private static int ms_estimatedKeyboardHeight = 100 + (Build.VERSION.SDK_INT >= 21 ? 48 : 0);

	private int m_currentKeyboardType = 0;
	private int m_currentReturnKeyType = 0;
	private int m_currentAutocapitalizationType = 0;
	private int m_currentKeyboardHeight = 0;
	private boolean m_currentPredictiveTextEnabled = false;

	private boolean m_setTextHandlerEnabled = false;

	///////////////////////////////////////////////////////////////////
	// METHODS
	///////////////////////////////////////////////////////////////////

	RunnerKeyboardController(Context _context, InputMethodManager _imm, View _activityView, Handler _viewHandler)
	{
		m_context = _context;
		m_inputMethodManager = _imm;
		m_activityView = _activityView;
		m_viewHandler = _viewHandler;
	}

	// Creates a new instance of the keyboard controller
	public static RunnerKeyboardController Create(Context _context, View _activityView, Handler _viewHandler)
	{
		InputMethodManager imm = (InputMethodManager) _context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm == null)
		{
			//Log.i("yoyo", "Could not create Virtual Keyboard Controller - could not retrieve InputMethodManager for current activity.");
			return null;
		}

		RunnerKeyboardController newController = new RunnerKeyboardController(_context, imm, _activityView, _viewHandler);
		newController.Init();
		//Log.i("yoyo", "Virtual Keyboard Controller initialised.");

		return newController;
	}
	
	// Initialises handlers and other references
	private void Init()
	{
		InitTextField();
		InitReceivers();
	}

	// Initialise text field used for displaying virtual keyboard/tracking input
	private void InitTextField()
	{
		final RunnerKeyboardController keyboardController = this;

		m_viewHandler.post(new Runnable() 
		{
			@Override
			public void run()
			{
				// Create text field
				m_editText = new KeyboardInputEditText(m_context, keyboardController);
				
				// Text changed listener for soft keyboard
				m_editText.addTextChangedListener(new TextWatcher() 
				{
					@Override
					public void beforeTextChanged(CharSequence text, int start, int lengthReplaced, int lengthNew) 
					{
					}

					@Override
					public void onTextChanged(CharSequence text, int start, int lengthReplaced, int lengthNew) 
					{			
						if(m_setTextHandlerEnabled == false)
						{
							//Log.i("yoyo", "[VK] ONTEXTCHANGED handler disabled - skipping..");
							return;
						}
		
						//Log.i("yoyo", "[VK] ONTEXTCHANGED. Text: " + text.toString() + ". Predictive text enabled: " + m_currentPredictiveTextEnabled + ". Start: " + start + ". Length Replaced: " + lengthReplaced + ". Amount replaced: " + lengthNew);
						//Log.i("yoyo", "[VK] Call stack: " + Log.getStackTraceString(new Exception()));						

						String textInserted = text.toString();
						if(textInserted.length() == 0)
						{
							if(m_currentPredictiveTextEnabled == true)
							{
								//Log.i("yoyo", "[VK] Inserting empty string chars.");
								char[] emptyStringChars = { 0 };
								RunnerJNILib.OnVirtualKeyboardTextInserted(emptyStringChars, 0);
							}

							return;
						}
				
						int lastCharInserted = textInserted.codePointAt(textInserted.length() - 1);
						//Log.i("yoyo", "[VK] Last character inserted: " + lastCharInserted + " ("+(char)lastCharInserted+")");

						if(m_currentPredictiveTextEnabled == false)
						{
							// For non-predictive text, handle the last character only
							//Log.i("yoyo", "[VK] ONTEXTCHANGED for single key. Processed char: " + lastCharInserted  + "( "+(char)lastCharInserted+" )");
							RunnerJNILib.KeyEvent(0, 0, lastCharInserted, KEYBOARD_EVENT_SOURCE_DEFAULT);
							RunnerJNILib.KeyEvent(1, 0, lastCharInserted, KEYBOARD_EVENT_SOURCE_DEFAULT);
						}
						else
						{
							// For predictive text, insert the entire string
							//Log.i("yoyo", "[VK] ONTEXTCHANGED for predictive text. Word: " + textInserted + ".");

							// Send codepoints to the runner
							char[] textChars = new char[textInserted.length()];
							textInserted.getChars(0, textInserted.length(), textChars, 0);
							RunnerJNILib.OnVirtualKeyboardTextInserted(textChars, textInserted.length());
						}					
					}

					@Override
					public void afterTextChanged(Editable text) 
					{	
					}
				});

				// Editor action listener for action key
				m_editText.setOnEditorActionListener(new OnEditorActionListener() 
				{
					@Override
					public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
					{
						//Log.i("yoyo", "[VK] Editor action: " + actionId);
						RunnerJNILib.KeyEvent(0, 0x0D, (int)('\n'), KEYBOARD_EVENT_SOURCE_DEFAULT);
						RunnerJNILib.KeyEvent(1, 0x0D, (int)('\n'), KEYBOARD_EVENT_SOURCE_DEFAULT);
						return true;
					}
				});

				// Set up edit text layout
				RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(DEBUG_SHOW_HIDDEN_TEXT_FIELD ? 1000 : 1, DEBUG_SHOW_HIDDEN_TEXT_FIELD ? 200 : 1);
				layout.leftMargin = DEBUG_SHOW_HIDDEN_TEXT_FIELD ? 0 : -1;
				layout.topMargin = DEBUG_SHOW_HIDDEN_TEXT_FIELD ? 0 : -1;				
				m_editText.setLayoutParams(layout);

				m_editText.setFocusable(true);
				m_editText.setFocusableInTouchMode(true);
				m_editText.setSingleLine(true);

				if(DEBUG_SHOW_HIDDEN_TEXT_FIELD == false)
				{
					m_editText.setBackgroundColor(Color.TRANSPARENT);
					m_editText.setTextColor(Color.TRANSPARENT);
					m_editText.setCursorVisible(false);
				}
				else
				{
					m_editText.setTextSize(10);
				}

				// Add text field to parent layout
				ViewGroup parentView = (ViewGroup)m_activityView;
				parentView.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
				parentView.setFocusableInTouchMode(true);
				parentView.addView(m_editText);

				// Clear input
				SetInputString("", true);
			}
		});
	}

	// Initialise keyboard event result receivers
	private void InitReceivers()
	{
		// Handles the result of a keyboard show/hide request via keyboard_virtual_show/keyboard_virtual_hide
		m_virtualKeyboardToggleResultReceiver = new KeyboardResultReceiver(this) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) 
			{
				super.onReceiveResult(resultCode, resultData);

				//Log.i("yoyo", "[VK] Keyboard state changed to: " + resultCode);

				switch(resultCode)
				{
					case InputMethodManager.RESULT_HIDDEN:
					case InputMethodManager.RESULT_UNCHANGED_HIDDEN:
						m_keyboardController.SetVirtualKeyboardActive(false);
						break;

					case InputMethodManager.RESULT_SHOWN:
					case InputMethodManager.RESULT_UNCHANGED_SHOWN:
						m_keyboardController.SetVirtualKeyboardActive(true);
						break;
				}
				
				m_keyboardController.UpdateKeyboardStatusFromIMMResult(resultCode);
				m_keyboardController.VirtualKeyboardReportStatus();
			}
		};
	
		// Reports the keyboard status after a keyboard status request check finishes reverting the keyboard state
		m_virtualKeyboardVisibilityCheckAdjustReceiver = new KeyboardResultReceiver(this)
		{
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) 
			{
				super.onReceiveResult(resultCode, resultData);
				m_keyboardController.VirtualKeyboardReportStatus();
			}
		};
	
		// Handles the result of a keyboard change performed in order to get its status via keyboard_get_status
		m_virtualKeyboardVisibilityCheckResultReceiver = new KeyboardResultReceiver(this) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) 
			{
				super.onReceiveResult(resultCode, resultData);
				m_keyboardController.OnVirtualKeyboardVisibilityCheckResult(resultCode);
			}
		};

		// Triggered when the global layout changes
		m_activityView.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardLayoutListener(this));
	}

	// Toggles the virtual keyboard on/off
	public void VirtualKeyboardToggle(final boolean _toggleOn, final int _keyboardType, final int _returnKeyType, 
									  final int _autoCapitalizationType, final boolean _predictiveTextEnabled, final String _inputString)
	{
		m_viewHandler.post(new Runnable() 
		{
			@Override
			public void run()
			{
				//Log.i("yoyo", "[VK] Toggling keyboard " + (_toggleOn ? "on" : "off") + ". Keyboard type: " + _keyboardType + ". Return key type: " + _returnKeyType + ". Autocap: " + _autoCapitalizationType + ". Predictive: " + _predictiveTextEnabled + ". String: "+ _inputString);		
				if(_toggleOn == true)
				{
					// Select IME and input types from params
					int inputType = 0;
					String returnText = null;

					// BUGFIX: We want to disable pulling down the hidden text field in order to prevent unexpected/unhandled behaviours
					int imeType = EditorInfo.IME_FLAG_NO_EXTRACT_UI
								| EditorInfo.IME_FLAG_NO_ACCESSORY_ACTION
								| EditorInfo.IME_FLAG_NO_FULLSCREEN;

					// Keyboard type
					switch(_keyboardType)
					{
						case 0: inputType |= InputType.TYPE_CLASS_TEXT;						break;
						case 1: inputType |= InputType.TYPE_TEXT_VARIATION_NORMAL;			break;
						case 2: inputType |= InputType.TYPE_TEXT_VARIATION_URI;				break;
						case 3: inputType |= InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS;	break;
						case 4: inputType |= InputType.TYPE_CLASS_NUMBER;					break;
						case 5: inputType |= InputType.TYPE_CLASS_PHONE;					break;
						case 6: inputType |= InputType.TYPE_TEXT_VARIATION_PERSON_NAME;     break;
					}

					// Auto capitalization
					switch(_autoCapitalizationType)
					{
						case 1:	inputType |= InputType.TYPE_TEXT_FLAG_CAP_WORDS; break;
						case 2:	inputType |= InputType.TYPE_TEXT_FLAG_CAP_SENTENCES; break;
						case 3:	inputType |= InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS; break;
					}

					// Predictive text
					if(_predictiveTextEnabled == false)
					{
						// HTC devices will restrict users' languages if the visible password input type is used
						if (android.os.Build.MANUFACTURER.equalsIgnoreCase("HTC")) 
							inputType |= InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS;
						else 
							inputType |= InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
					}

					// Return key type
					switch(_returnKeyType)
					{
						case 1:  imeType |= EditorInfo.IME_ACTION_GO;			returnText = "Go";				break;
						case 2:  imeType |= EditorInfo.IME_ACTION_SEARCH;		returnText = "Google";			break;
						case 3:  imeType |= EditorInfo.IME_ACTION_GO;			returnText = "Join";			break;
						case 4:  imeType |= EditorInfo.IME_ACTION_NEXT;			returnText = "Next";			break;
						case 5:  imeType |= EditorInfo.IME_ACTION_GO;			returnText = "Route";			break;
						case 6:  imeType |= EditorInfo.IME_ACTION_SEARCH;										break;
						case 7:  imeType |= EditorInfo.IME_ACTION_SEND;											break;
						case 8:  imeType |= EditorInfo.IME_ACTION_SEARCH;		returnText = "Yahoo";			break;
						case 9:  imeType |= EditorInfo.IME_ACTION_DONE;											break;
						case 10: imeType |= EditorInfo.IME_ACTION_NEXT;			returnText = "Continue";		break;
						case 11: imeType |= EditorInfo.IME_ACTION_GO;			returnText = "Emergency Call";	break;
						default: imeType |= EditorInfo.IME_ACTION_UNSPECIFIED;									break;
					}

					// Remove autocorrect & autocapitalization - we can't support these very well at the moment
					imeType &= ~InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE;
					imeType &= ~InputType.TYPE_TEXT_FLAG_AUTO_CORRECT;
					
					// Save current parameters
					m_currentKeyboardType = _keyboardType;
					m_currentReturnKeyType = _returnKeyType;
					m_currentAutocapitalizationType = _autoCapitalizationType;
					m_currentPredictiveTextEnabled = _predictiveTextEnabled;

					// Set up keyboard
					m_editText.setImeOptions(imeType);
					m_editText.setImeActionLabel(returnText, imeType);
	    			m_editText.setInputType(inputType);
					m_editText.requestFocus();
	    			
					SetInputString(_inputString, true);
					
					// Show keyboard
					m_inputMethodManager.showSoftInput(m_editText, 0, m_virtualKeyboardToggleResultReceiver);
				}
				else
				{
					// Hide keyboard
					m_editText.clearFocus();
					m_inputMethodManager.hideSoftInputFromWindow(m_editText.getWindowToken(), 0, m_virtualKeyboardToggleResultReceiver);
				}
			}
		});
	}

	// Hide the virtual keyboard
	public void VirtualKeyboardHide()
	{
		VirtualKeyboardToggle(false, 0, 0, 0, true, GetInputString());
	}

	// Queries the status of the virtual or physical keyboard
	// Results are returned to the runner via an async system event
	public boolean VirtualKeyboardGetStatus()
	{
		return m_virtualKeyboardActive;
	}

	// Reports the status of either the physical or virtual keyboards
	public void VirtualKeyboardReportStatus()
	{
		//Log.i("yoyo", "[VK] Reporting keyboard status. Active: " + m_virtualKeyboardActive + ". Visible: " + m_virtualKeyboardVisible + ". Status " + m_keyboardStatus);
		m_virtualKeyboardStatusRequested = false;
		m_currentKeyboardHeight = VirtualKeyboardGetHeight();
		RunnerJNILib.OnVirtualKeyboardStatus(m_keyboardStatus, m_currentKeyboardHeight);
	}

	public void OnVirtualKeyboardVisibilityCheckResult(final int _resultCode)
	{
		// Since there's no proper way in Android to check if a soft keyboard is visible (except for some nasty hardcoded screen size checks),
		// we need to resort to this mess where we try showing a soft keyboard and check if its state has changed.
		// If it has, we know whether or not it was visible/invisible previously.
		// Once we know that, we need to toggle it back to its previous state if we did change it

		// Retrieve the keyboard state
		switch(_resultCode)
		{
			case InputMethodManager.RESULT_HIDDEN:
			case InputMethodManager.RESULT_UNCHANGED_HIDDEN:
			case InputMethodManager.RESULT_SHOWN:
				SetVirtualKeyboardActive(false);
				m_keyboardStatus = KEYBOARD_STATUS_HIDDEN;
				break;

			case InputMethodManager.RESULT_UNCHANGED_SHOWN:
				SetVirtualKeyboardActive(true);
				m_keyboardStatus = KEYBOARD_STATUS_VISIBLE;
				break;
		}

		m_viewHandler.post(new Runnable() 
		{
			@Override
			public void run()
			{
				// Revert keyboard to its previous state if we changed it
				if(_resultCode == InputMethodManager.RESULT_HIDDEN)
				{
					m_inputMethodManager.showSoftInput(m_editText, 0, m_virtualKeyboardVisibilityCheckAdjustReceiver);
				}
				else if(_resultCode == InputMethodManager.RESULT_SHOWN)
				{
					m_inputMethodManager.hideSoftInputFromWindow(m_editText.getWindowToken(), 0, m_virtualKeyboardVisibilityCheckAdjustReceiver);
				}
				else
				{
					VirtualKeyboardReportStatus();
				}
			}
		});
	}

	// Returns the height of the virtual keyboard (estimated value, might not work for certain user configurations)
	public int VirtualKeyboardGetHeight()
	{
		m_activityView.getWindowVisibleDisplayFrame(m_viewActiveRect);
		int keyboardHeight = m_activityView.getHeight() - (m_viewActiveRect.bottom - m_viewActiveRect.top);
		return keyboardHeight;
	}

	// Called when a key event is received from a physical keyboard instead of the default soft one
	public void OnPhysicalKeyboardKeyEvent(int _keyCode, KeyEvent _event)
	{
		// If the virtual keyboard was visible, and we pressed a key on the physical keyboard, update our virtual keyboard
		// visibility to false. The system automatically hides any soft input when physical input is detected.
		// TODO: QA verification needed on different input types/devices

		/*
		if(m_virtualKeyboardActive == true)
		{
			Log.i("yoyo", "[VK] Physical keyboard press detected. Updating virtual keyboard status to hidden.");
			SetVirtualKeyboardActive(false);
			VirtualKeyboardReportStatus();
		}
		*/
	}

	// Called when the activity layout changes. We use this to estimate whether or not the keyboard is visible
	public void OnActivityLayoutChanged()
	{
		// Check if the keyboard should be estimated as visible
		int keyboardHeight = VirtualKeyboardGetHeight();
		int estimatedKeyboardHeightTransformed = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 
																				ms_estimatedKeyboardHeight, 
																				m_activityView.getResources().getDisplayMetrics());
		boolean keyboardVisible = (keyboardHeight >= estimatedKeyboardHeightTransformed);
	    if (keyboardVisible == m_virtualKeyboardVisible) 
	        return;

		SetVirtualKeyboardVisible(keyboardVisible);

		if(keyboardVisible)
			m_keyboardStatus = KEYBOARD_STATUS_VISIBLE;
		else
			m_keyboardStatus = KEYBOARD_STATUS_HIDDEN;

		VirtualKeyboardReportStatus();
	}

	public void UpdateKeyboardStatusFromIMMResult(int _resultCode)
	{
		switch(_resultCode)
		{
			case InputMethodManager.RESULT_HIDDEN:				m_keyboardStatus = KEYBOARD_STATUS_HIDING;  break;
			case InputMethodManager.RESULT_UNCHANGED_HIDDEN:	m_keyboardStatus = KEYBOARD_STATUS_HIDDEN;  break;
			case InputMethodManager.RESULT_SHOWN:				m_keyboardStatus = KEYBOARD_STATUS_SHOWING; break;
			case InputMethodManager.RESULT_UNCHANGED_SHOWN:		m_keyboardStatus = KEYBOARD_STATUS_VISIBLE; break;
		}
	}
	
	public void SetVirtualKeyboardActive(boolean _active)
	{
		//Log.i("yoyo", "[VK] Virtual keyboard active status updated to: " + _active);
		m_virtualKeyboardActive = _active;
	}

	public boolean GetVirtualKeyboardActive()
	{
		return m_virtualKeyboardActive;
	}

	public void SetVirtualKeyboardVisible(boolean _visible)
	{
		//Log.i("yoyo", "[VK] Virtual keyboard visibility status updated to: " + _visible);
		m_virtualKeyboardVisible = _visible;
	}

	public boolean GetVirtualKeyboardVisible()
	{
		return m_virtualKeyboardVisible;
	}

	public void SetPhysicalKeyboardConnected(boolean _connected)
	{
		//Log.i("yoyo", "[VK] Physical keyboard connected status updated to: " + _connected);
		m_physicalKeyboardConnected = _connected;
	}

	public boolean GetPhysicalKeyboardConnected()
	{
		return m_physicalKeyboardConnected;
	}

	public int GetVirtualKeyboardHeightCached()
	{
		return m_currentKeyboardHeight;
	}

	public boolean GetPredictiveTextEnabled()
	{
		return m_currentPredictiveTextEnabled;
	}

	// Update the keyboard string currently in the hidden text field
	public void SetInputString(String _newString, boolean _silent)
	{
		if(m_editText != null)
		{
			//Log.i("yoyo", "[VK] Keyboard input string set to: " + _newString + ". Silent: " + _silent);

			if(_silent) 
				m_setTextHandlerEnabled = false;				

			m_editText.setText(_newString);
			m_editText.setSelection(_newString.length());

			if(_silent) 
				m_setTextHandlerEnabled = true;	
		}
	}

	public String GetInputString()
	{
		if(m_editText != null)
		{
			return m_editText.getText().toString();
		}

		return "";
	}
}