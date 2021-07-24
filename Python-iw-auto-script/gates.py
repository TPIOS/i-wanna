import pyautogui
import os, sys, time
from pynput.keyboard import Key, Controller

def jump(keyboard, frames = 1):
    pass


def left2rightGate(keyboard):
    keyboard.press(Key.shift.value)
    keyboard.press(Key.right.value)
    time.sleep(0.07)
    keyboard.release(Key.shift.value)
    time.sleep(0.35)
    keyboard.release(Key.right.value)

def right2leftGate(keyboard):
    keyboard.press(Key.shift.value)
    keyboard.press(Key.left.value)
    time.sleep(0.07)
    keyboard.release(Key.shift.value)
    time.sleep(0.35)
    keyboard.release(Key.left.value)

time.sleep(5)
while True:
    keyboard = Controller()
    left2rightGate(keyboard)
    time.sleep(2)
    right2leftGate(keyboard)
    # jump(0.02)
    time.sleep(2)