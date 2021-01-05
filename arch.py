#! /usr/bin/env python3
#
# Handles maven peculiarities when making service instances from archetypes 
# TODO convert this into a stand-alone camel-main app


import argparse
import subprocess
import os 
import logging



#
# MAVEN COMMAND LINE CONSTANTS 
#

MAVEN_COMMAND = "mvn"
MAVEN_GOAL = "archetype:generate"

ARCHETYPE_GROUP_ID_OPTION = "-DarchetypeGroupId="
ARCHETYPE_GROUP_ID_BASE = "io.holitek.kcar"

ARCHETYPE_VERSION_OPTION = "-DarchetypeVersion="
ARCHETYPE_VERSION = "1.0-SNAPSHOT"

ARCHETYPE_ARTIFACT_ID_OPTION = "-DarchetypeArtifactId="


GROUP_ID_OPTION = "-DgroupId="
GROUP_ID_BASE = "io.holitek.kcar"

VERSION_OPTION = "-Dversion="
VERSION = "1.0-SNAPSHOT"

ARTIFACT_ID_OPTION = "-DartifactId="


#
# JAVA SERVICE FACTORY MODULE NAMES
#

ELEMENTS_MODULE_NAME = "elements"
ROUTES_MODULE_NAME = "routes"
CHASSIS_MODULE_NAME = "chassis"
SERVICES_MODULE_NAME = "services"


#
# PARSING HELPERS 
#

def get_archetype_group_id(module_name):
    return ARCHETYPE_GROUP_ID_BASE + "." + module_name

def get_group_id(module_name):
    return GROUP_ID_BASE + "." + module_name




#
# FUNCTIONS THAT HANDLE USER REQUESTS
#

def bean(artifact_id):
    logging.info("generating a bean module named: {}".format(artifact_id))    
    os.chdir("./" + ELEMENTS)
    
    
    try:
        # purposefully NOT setting shell=True
        # https://docs.python.org/3/library/subprocess.html#security-considerations
        result = subprocess.run(
            [
                MAVEN_COMMAND,
                MAVEN_GOAL,
                ARCHETYPE_GROUP_ID_OPTION + get_archetype_group_id(ELEMENT_MODULE_NAME),
                ARCHETYPE_VERSION_OPTION + archetype_version,
                ARCHETYPE_ARTIFACT_ID_OPTION + "bean-archetype",
                GROUP_ID_OPTION + get_group_id(ELEMENT_MODULE_NAME)
                VERSION_OPTION + VERSION,
                ARTIFACT_ID_OPTION + artifact_id
            ], 
            capture_output=True,
            check=True
        )
    except CalledProcessError:
        logging.error("something went wrong making the bean", result.stderr)
    
    os.chdir("../")
    logging.info("done!")



#
# MENU STRUCTURES
#

THING_TYPES = [
    bean, 
]


UNIMPLEMENTED_THING_TYPES = [
    "processor", 
    "connector (NOT YET IMPLEMENTED)",
    "route (NOT YET IMPLEMENTED)", 
    "chassis (NOT YET IMPLEMENTED)", 
    "service (NOT YET IMPLEMENTED)",
    "raw camel archetype (NOT YET IMPLEMENTED)"
]


MENU_DICT = {
    i: THING_TYPES[i]
    for i 
    in range(0, len(THING_TYPES))
}


#
# MENU CODE
#

def print_menu():
    print("-= What kind of thing would you like to make? =-")
    for i in range(0, len(THING_TYPES)):
        print("[{}]  {}".format(str(i), MENU_DICT[i].__name__))  
    
    for i in range(0, len(UNIMPLEMENTED_THING_TYPES)):
        print("[]  {}".format(UNIMPLEMENTED_THING_TYPES[i]))  
    
    print("----")


def main(args):

    choice_int = -1
    done = False
    while not done:
        try:
            done = True
            print_menu()
            raw_choice = input("enter choice or ctrl-c to exit >> ")
            choice_int = int(raw_choice)
            if choice_int not in MENU_DICT.keys():
                raise ValueError

        except KeyboardInterrupt:
            logging.info("\nbye!")
        except ValueError:
            done = False
            logging.error("\n** input needs to be a number and one that's listed in the menu! **\n".format(raw_choice))


    # the values of MENU_DICT are python functions - hence the second set of '()' ...
    MENU_DICT.get(choice_int)(args.artifact_id)
    
    
#
# BOOTSTRAP HOOK 
#
     
if __name__ == "__main__":
    description_text = "helper to create starter code for Java Service Factory elements, routes, chassis, and services."

    parser = argparse.ArgumentParser(description=description_text)
    
    parser.add_argument(
        "artifact_id", 
        help="name of the thing you want to make. if name is more than one word, use hyphens eg 'health-check-route'", 
        type=str
    )

    args = parser.parse_args()
    main(args)

