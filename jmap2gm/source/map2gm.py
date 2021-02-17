# entry point, input sanitization, general controller stuff

import os
import requests
import sys
# map2gm modules
import gui
import convert
import localize
import util

def loc(key):
    return localize.loc(key)

def submitted(project_path, template_room_name, map_path, object_inputs):

    # input sanitization
    if project_path == '':
        return loc('project_empty')
    if template_room_name == '':
        return loc('template_room_empty')
    if map_path == '':
        return loc('map_empty')

    if not os.path.exists(project_path):
        return loc('project_nonexistent')
    project_extension = project_path.split('.')[-1]
    if project_extension == 'gmx':
        template_room_path = os.path.join(os.path.split(project_path)[0], 'rooms', template_room_name+'.room.gmx')
    else:
        template_room_path = os.path.join(util.get_application_path(), 'temp_gmksplit', 'Rooms', template_room_name+'.xml')
    if not os.path.exists(template_room_path):
        return loc('template_room_nonexistent')
    if not os.path.exists(map_path):
        return loc('map_nonexistent')

    for objectname, enabled in object_inputs.values():
        if enabled:
            if objectname == '':
                return loc('enabled_object_no_name')
            if project_extension == 'gmx':
                object_path = os.path.join(os.path.split(project_path)[0], 'objects', objectname + '.object.gmx')
                if not os.path.exists(object_path):
                    return loc('object_nonexistent') % objectname

    # build dict of object names that were enabled
    chosen_object_names = {}
    for object_name, (gm_object_name, enabled) in object_inputs.items():
        if enabled:
            chosen_object_names[object_name] = gm_object_name

    conversion_output_filename = convert.convert(project_path, template_room_path, map_path, chosen_object_names)

    # save preferences
    with open('prefs', 'w') as f:
        f.write('project|%s\n' % project_path)
        f.write('template|%s\n' % template_room_name)
        for object_name, (gm_object_name, enabled) in sorted(object_inputs.items()):
            f.write('%s|%s|%i\n' % (object_name, gm_object_name, enabled))

    if project_extension == 'gmx':
        base = loc('convert_success_gmx')
    else:
        base = loc('convert_success_gmk')
    return base % conversion_output_filename

# prompt and load language
if os.path.exists('lang'):
    with open('lang', 'r') as f:
        language = list(f)[0]
    localize.load(language)
else:
    language = gui.ask_language()
    with open('lang', 'w') as f:
        f.write(language)
    localize.load(language)
    gui.show_instructions()

# check for update
try:
    r = requests.get('http://cwpat.me/map2gm-version')
    if r.status_code == 200:
        my_version = '2.2'
        newest_version = r.json()['map2gm-version']
        if my_version != newest_version:
            gui.show_update(newest_version)
except Exception as e:
    print('Error when checking for new version.')
    print(e)

# kick off the gui
gui.run(submit_func=submitted)
