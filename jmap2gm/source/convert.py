# actual conversion logic - parses map files, outputs GM files

import os
import sys
import random
import math
import subprocess
import lxml.etree as ET
# map2gm modules
import util

object_ids = {
    'block':(2,1),
    'miniblock':(None,2),
    'spikeup':(12,3),
    'spikeright':(11,4),
    'spikeleft':(10,5),
    'spikedown':(9,6),
    'miniup':(19,7),
    'miniright':(18,8),
    'minileft':(17,9),
    'minidown':(16,10),
    'save':(32,12),
    'platform':(31,13),
    'water1':(23,14),
    'water2':(30,15),
    'water3':(None,23),
    'cherry':(20,11),
    'hurtblock':(27,18),
    'vineright':(28,17),
    'vineleft':(29,16),
    'jumprefresher':(None,22),
    'bulletblocker':(None,19),
    'start':(3,20),
    'warp':(None,21),
    }

def convert(project_path, template_room_path, map_path, chosen_names):

    # build conversion dicts according to chosen object names
    rmj_to_objectname = {}
    jtool_to_objectname = {}
    for name, gm_name in chosen_names.items():
        rmj_id, jtool_id = object_ids[name]
        if rmj_id != None:
            rmj_to_objectname[str(rmj_id)] = gm_name
        jtool_to_objectname[str(jtool_id)] = gm_name

    # read instances from map file
    map_instances = []
    extension = map_path.split('.')[-1]
    if extension == 'map':
        with open(map_path) as f:
            line = f.readlines()[3]
        numbers = line[1:].split(' ')
        for i in range(0, len(numbers), 3):
            x, y, id = numbers[i:i+3]
            if id in rmj_to_objectname:
                map_instances.append((x,y,rmj_to_objectname[id]))
    elif extension == 'jmap':
        with open(map_path) as f:
            line = f.readline()
            if line[-1] == '\n':
                line = line[0:-1]
            sections = line.split('|')
        def base32string_decode(string):
            base32string = '0123456789abcdefghijklmnopqrstuv'
            result = 0
            for i, char in enumerate(string):
                charvalue = base32string.index(char)
                placevalue = math.pow(32, len(string)-i-1)
                result += charvalue * placevalue
            return str(int(result))
        for section in sections:
            if section.split(':')[0] == 'objects':
                objectstring = section.split(':')[1]
        for ypos_section in objectstring.split('-'):
            y = ypos_section[0:2]
            y = base32string_decode(y)
            y = str(int(y)-128)
            ypos_section = ypos_section[2:]
            for i in range(0, len(ypos_section), 3):
                id = ypos_section[i]
                id = base32string_decode(id)
                x = ypos_section[i+1:i+3]
                x = base32string_decode(x)
                x = str(int(x)-128)
                if id in jtool_to_objectname:
                    map_instances.append((x,y,jtool_to_objectname[id]))

    # construct valid output room name and path
    # output_room_name = 'rMapImport_%s' % os.path.split(map_path)[1].split('.')[0]
	output_room_name = os.path.split(map_path)[1].split('.')[0]
    valid_chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_'
    output_room_name = ''.join([char if char in valid_chars else '_' for char in output_room_name])
    project_extension = project_path.split('.')[-1]
    if project_extension == 'gmx':
        output_room_path = os.path.join(util.get_application_path(), output_room_name+'.room.gmx')
    else:
        output_room_path = os.path.join(util.get_application_path(), 'temp_gmksplit', 'Rooms', output_room_name+'.xml')

    # create a new room file (based on template) with the instances added
    parser = ET.XMLParser(remove_blank_text=True)
    output_room_tree = ET.parse(template_room_path, parser)
    instances_element = output_room_tree.getroot().find('instances')
    for x, y, name in map_instances:
        if project_extension == 'gmx':
            attrib = {}
            attrib['x'] = x
            attrib['y'] = y
            attrib['objName'] = name
            attrib['locked'] = '0'
            attrib['code'] = ''
            attrib['scaleX'] = '1'
            attrib['scaleY'] = '1'
            attrib['colour'] = '4294967295'
            attrib['rotation'] = '0'
            attrib['name'] = 'inst_'+''.join([random.choice('0123456789ABCDEF') for i in range(8)])
            ET.SubElement(instances_element, 'instance', attrib=attrib)
        else:
            inst_elt = ET.Element('instance')
            obj_elt = ET.SubElement(inst_elt, 'object')
            obj_elt.text = name
            ET.SubElement(inst_elt, 'position', x=x, y=y)
            ET.SubElement(inst_elt, 'creationCode')
            locked_elt = ET.SubElement(inst_elt, 'locked')
            locked_elt.text = 'false'
            instances_element.append(inst_elt)
    output_room_tree.write(output_room_path, pretty_print = True)

    # if gmksplit project, add room to room list and recompose project file
    if project_extension != 'gmx':
        room_resources_path = os.path.join(util.get_application_path(), 'temp_gmksplit', 'Rooms', '_resources.list.xml')
        parser = ET.XMLParser(remove_blank_text=True)
        room_resources_tree = ET.parse(room_resources_path, parser)
        room_already_in_resource_list = False
        for room_element in room_resources_tree.getroot():
            if room_element.attrib['name'] == output_room_name:
                room_already_in_resource_list = True
        if not room_already_in_resource_list:
            ET.SubElement(room_resources_tree.getroot(), 'resource', name=output_room_name, type='RESOURCE')
            room_resources_tree.write(room_resources_path, pretty_print = True)

        output_project_name = os.path.split(project_path)[1].split('.')[-2]
        output_project_name += '_' + ''.join([random.choice('abcdefghijklmnopqrstuvwxyz') for i in range(5)]) + '.' + project_extension
        output_project_path = os.path.join(util.get_application_path(), output_project_name)
        subprocess.call(os.path.join(util.get_application_path(), 'gmksplitter\\gmksplit.exe temp_gmksplit "%s"' % output_project_path))

    if project_extension == 'gmx':
        return output_room_name
    else:
        return output_project_name
