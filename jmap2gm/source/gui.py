# controls window for user input

import sys
import os
import subprocess
import shutil
import traceback
import webbrowser
import tkinter as tk
from tkinter import filedialog, messagebox
# map2gm modules
import localize
import util

def loc(key):
    return localize.loc(key)

def delete_temp_gmksplit_if_necessary():
    temp_gmksplit_path = os.path.join(util.get_application_path(), 'temp_gmksplit')
    if os.path.exists(temp_gmksplit_path):
        shutil.rmtree(temp_gmksplit_path)

def gmksplit_decompose(project_path):
    delete_temp_gmksplit_if_necessary()
    subprocess.call(os.path.join(util.get_application_path(), 'gmksplitter\\gmksplit.exe "%s" temp_gmksplit' % project_path))

def call_func_loading_popup(func, text):
    popup = tk.Toplevel()
    popup.wm_title('')
    popup.resizable(False, False)
    icon_image = tk.Image('photo', file='images/icon.png')
    popup.tk.call('wm','iconphoto',popup._w,icon_image)
    label = tk.Label(popup, text=text)
    label.grid(padx=30, pady=30)

    x = root.winfo_x() + 50
    y = root.winfo_y() + 50
    popup.geometry('+%d+%d' % (x, y))
    popup.update()
    func()
    popup.destroy()

def update_object_widgets(check_enabled, entry, button):
    if check_enabled:
        state = tk.NORMAL
    else:
        state = tk.DISABLED
    entry.configure(state=state)
    button.configure(state=state)

def ask_path(entry, dialogtitle, filetypes, initialdir, format_func):
    path = filedialog.askopenfilename(filetypes=filetypes,title=dialogtitle,initialdir=initialdir)
    if path != '':
        path = format_func(path)
        entry.delete(0, tk.END)
        entry.insert(0, path)
        entry.xview(tk.END)

def project_row_clicked(entry, project_entry):
    dialogtitle = loc('open_project')
    filetypes = [('GameMaker Studio, 8.1, or 8.0 project', '*.gm*')]
    initialdir = util.get_application_path()
    format_func = lambda path: path

    lastpath = entry.get()
    ask_path(entry, dialogtitle, filetypes, initialdir, format_func)

    filetype = entry.get().split('.')[-1]
    if filetype != 'gmx' and entry.get() != lastpath and os.path.exists(entry.get()):
        call_func_loading_popup(lambda: gmksplit_decompose(entry.get()), loc('gmksplit_working'))

def template_row_clicked(entry, project_entry):
    filetype = project_entry.get().split('.')[-1]
    if filetype == 'gmx':
        filetypes = [('GameMaker: Studio room', '*.room.gmx')]
        initialdir = os.path.join(os.path.split(project_entry.get())[0],'rooms')
    else:
        filetypes = [('Gmksplit room', '*.xml')]
        initialdir = os.path.join(util.get_application_path(),'temp_gmksplit','Rooms')
        if not os.path.exists(initialdir):
            call_func_loading_popup(lambda: gmksplit_decompose(project_entry.get()), loc('gmksplit_working'))
    dialogtitle = loc('open_template_room')
    format_func = lambda path: os.path.split(path)[1].split('.')[0]
    ask_path(entry, dialogtitle, filetypes, initialdir, format_func)

def map_row_clicked(entry, project_entry):
    dialogtitle = loc('open_map')
    filetypes = [('Jtool or Record My Jumps map', '*.*map')]
    initialdir = util.get_application_path()
    format_func = lambda path: path
    ask_path(entry, dialogtitle, filetypes, initialdir, format_func)

def object_row_clicked(entry, project_entry):
    filetype = project_entry.get().split('.')[-1]
    if filetype == 'gmx':
        filetypes = [('GameMaker: Studio object', '*.object.gmx')]
        initialdir = os.path.join(os.path.split(project_entry.get())[0],'objects')
    else:
        filetypes = [('Gmksplit object', '*.xml')]
        initialdir = os.path.join(util.get_application_path(),'temp_gmksplit','Objects')
        if not os.path.exists(initialdir):
            call_func_loading_popup(lambda: gmksplit_decompose(project_entry.get()), loc('gmksplit_working'))
    dialogtitle = loc('open_object')
    format_func = lambda path: os.path.split(path)[1].split('.')[0]
    ask_path(entry, dialogtitle, filetypes, initialdir, format_func)

def entry_button_row(row, labeltext):
    label = tk.Label(root,text=labeltext)
    entry = tk.Entry(root)
    button = tk.Button(root,image=folder_image,width=35,height=25)
    label.grid(row=row,column=0,sticky=tk.E)
    entry.grid(row=row,column=1,sticky=tk.EW)
    button.grid(row=row,column=2,sticky=tk.W)
    return (entry, button)

def show_update(newversion):
    window = tk.Tk()
    window.resizable(False, False)
    window.geometry('350x150')
    window.wm_title('')
    label = tk.Label(window,text=loc('update_label').format(version=newversion))
    label.grid(row=0,column=0,sticky=tk.NSEW,padx=20,pady=20)
    def callback():
        webbrowser.open('https://www.bit.ly/needle-map-to-gm')
        window.destroy()
    button = tk.Button(window, text=loc('update_button'), command=callback)
    button.grid(row=1,column=0,sticky=tk.NSEW,padx=20,pady=20)
    window.mainloop()

def ask_language():
    global chosen
    window = tk.Tk()
    window.resizable(False, False)
    window.geometry('300x100')
    window.wm_title('')
    window.protocol('WM_DELETE_WINDOW', lambda: None)
    icon_image = tk.Image('photo', file='images/icon.png')
    window.tk.call('wm','iconphoto',window._w,icon_image)
    def choose(c):
        global chosen
        window.destroy()
        chosen = c
    tk.Button(window, text='English', command=lambda: choose('English')).grid(row=0,column=0,sticky=tk.NSEW,padx=10,pady=10)
    tk.Button(window, text='日本語', command=lambda: choose('Japanese')).grid(row=0,column=1,sticky=tk.NSEW,padx=10,pady=10)
    window.grid_columnconfigure(0, weight=1)
    window.grid_columnconfigure(1, weight=1)
    window.grid_rowconfigure(0, weight=1)
    window.mainloop()
    return chosen

def change_language(language):
    with open('lang', 'w') as f:
        f.write(language)
    os.execl(sys.executable, sys.executable, * sys.argv)

def show_instructions():
    os.startfile('instructions.html')

def show_readme():
    os.startfile('readme.txt')

def submit(submit_function, convert_button, *args):
    convert_button.config(text=loc('button_convert_working') + '  ')
    root.update()

    filetype = args[0].split('.')[-1]
    if filetype != 'gmx' and not os.path.exists(os.path.join(util.get_application_path(),'temp_gmksplit')):
        gmksplit_decompose(args[0])

    try:
        message = submit_function(*args)
    except:
        message = loc('python_error') + '\n' + traceback.format_exc()
        traceback.print_exc()

    convert_button.config(text=loc('button_convert') + '  ')
    root.update()
    messagebox.showinfo('', message)

def run(submit_func):
    # init stuff
    global root, folder_image
    root = tk.Tk()
    folder_image = tk.Image('photo', file='images/folder.png')

    # top three input fields
    current_row = 0
    labeltext = loc('label_map')
    map_textbox, map_button = entry_button_row(current_row, labeltext)

    current_row += 1
    labeltext = loc('label_project')
    project_textbox, project_button = entry_button_row(current_row, labeltext)

    current_row += 1
    labeltext = loc('label_template_room')
    template_textbox, template_button = entry_button_row(current_row, labeltext)

    map_button.config(command=lambda: map_row_clicked(map_textbox, project_textbox))
    template_button.config(command=lambda: template_row_clicked(template_textbox, project_textbox))
    project_button.config(command=lambda: project_row_clicked(project_textbox, project_textbox))

    # object input fields
    current_row += 1
    object_names = [
        'block','spikeup','spikeright','spikeleft','spikedown',
        'miniblock','miniup','miniright','minileft','minidown',
        'save','platform','water1','water2','water3','cherry','hurtblock',
        'vineright','vineleft','jumprefresher','bulletblocker','start','warp',
        ]
    object_images = [(o,'images/%s.png'%o) for o in object_names]
    objectrowheight = 40
    frameheight = 5 * objectrowheight
    canvasheight = (len(object_images) - 1) * objectrowheight
    frame = tk.Frame(root,height=frameheight)
    frame.grid(row=current_row,column=0,columnspan=3,padx=5,pady=5,sticky=tk.NS)
    canvas = tk.Canvas(frame,scrollregion=(0,0,0,canvasheight),height=frameheight, width=340, yscrollincrement=objectrowheight)
    vbar=tk.Scrollbar(frame,orient=tk.VERTICAL)
    vbar.pack(side=tk.RIGHT,fill=tk.Y)
    vbar.config(command=canvas.yview)
    canvas.config(yscrollcommand=vbar.set)
    canvas.pack(side=tk.LEFT,expand=True,fill=tk.BOTH,pady=20)

    objectrow = 0
    object_widgets = {}
    for objectname, imagepath in object_images:
        photo = tk.PhotoImage(file=imagepath)
        w = tk.Label(root,image=photo)
        w.photo = photo # to prevent it from being garbage collected
        canvas.create_window((25,0+objectrow*objectrowheight),anchor=tk.CENTER,window=w)

        e = tk.Entry(root,state=tk.DISABLED)
        canvas.create_window((60,0+objectrow*objectrowheight),anchor=tk.W,window=e,width=120)

        cmd = lambda entry=e: object_row_clicked(entry, project_textbox)
        b = tk.Button(root,image=folder_image,command=cmd,state=tk.DISABLED)
        canvas.create_window((190,0+objectrow*objectrowheight),anchor=tk.W,window=b,width=40,height=30)

        v = tk.BooleanVar()
        v.set(False)
        cmd = lambda var=v,entry=e,button=b: update_object_widgets(var.get(),entry,button)
        c = tk.Checkbutton(root,text=loc('label_object_enabled'),variable=v,command=cmd)
        canvas.create_window((240,0+objectrow*objectrowheight),anchor=tk.W,window=c)

        object_widgets[objectname] = (e, v, b)
        objectrow += 1

    # convert button
    current_row += 1
    filler_label = tk.Label(root)
    filler_label.grid(row=current_row,column=0,columnspan=2,sticky=tk.NSEW)
    icon_image = tk.Image('photo', file='images/icon.png')
    convert_button = tk.Button(root,text=loc('button_convert') + '  ',image=icon_image,compound=tk.RIGHT,width=150)
    cmd = lambda: submit(submit_func, convert_button, project_textbox.get(), template_textbox.get(), map_textbox.get(), {k: (v[0].get(),v[1].get()) for (k,v) in object_widgets.items()})
    convert_button.configure(command=cmd)
    convert_button.grid(row=current_row,column=1,columnspan=2,sticky=tk.SE)

    # load values from prefs file
    if os.path.exists('prefs'):
        with open('prefs', 'r') as f:
            for line in f:
                args = line[:-1].split('|')
                args += [''] * (3 - len(args))
                type, value, checked = args
                if type == 'template':
                    template_textbox.insert(0, value)
                    template_textbox.xview(tk.END)
                elif type == 'project':
                    project_textbox.insert(0, value)
                    project_textbox.xview(tk.END)
                else:
                    entry, var, button = object_widgets[type]
                    entry.configure(state=tk.NORMAL)
                    entry.insert(0, value)
                    var.set(int(checked))
                    update_object_widgets(var.get(), entry, button)

    delete_temp_gmksplit_if_necessary()

    # menu bar
    menubar = tk.Menu(root)
    optionsmenu = tk.Menu(menubar, tearoff=False)
    menubar.add_cascade(label=loc('menu_toplevel'), menu=optionsmenu)
    optionsmenu.add_command(label=loc('menu_instructions'), command=show_instructions)
    optionsmenu.add_command(label=loc('menu_readme'), command=show_readme)
    optionsmenu.add_command(label=loc('menu_forum_thread'), command=lambda: webbrowser.open('https://www.bit.ly/needle-map-to-gm'))
    optionsmenu.add_command(label=loc('menu_more_tools'), command=lambda: webbrowser.open('http://cwpat.me/iwanna-tools/'))
    root.config(menu=menubar)

    # configure window and enter its main loop
    root.update()
    root.minsize(root.winfo_width(), root.winfo_height())
    root.grid_columnconfigure(0, weight=1, minsize=110)
    root.grid_columnconfigure(1, weight=8, minsize=150)
    root.grid_columnconfigure(2, weight=1, minsize=35)
    root.grid_rowconfigure(current_row-1, weight=1)
    root.resizable(True, True)
    root.wm_title(loc('title'))
    root.tk.call('wm','iconphoto',root._w,icon_image)
    root.mainloop()
