#! /usr/bin/env python3
#
# Handles maven peculiarities when making service instances from archetypes 
# TODO convert this into a stand-alone camel-main app


import argparse
import os 
import logging
import fileinput
import string
import shutil
import subprocess

from subprocess import PIPE, STDOUT


log_format = ("[%(name)s] [%(levelname)s] [%(asctime)s] -- %(message)s")
logging.basicConfig(level=logging.INFO, format=log_format)
logger = logging.getLogger("ARCH")


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

ARTIFACT_ID_CAMEL_CASE_OPTION = "-DartifactIdCamelCase="

ARTIFACT_ID_NAMESPACE_OPTION = "-DartifactIdNamespace="

ARTIFACT_SUBPACKAGE_NAME_OPTION = "-DsubpackageName="

INTERACTIVE_MODE_OPTION_AS_FALSE = "-DinteractiveMode=false"


#
# JAVA SERVICE FACTORY MODULE NAMES
#

ELEMENTS_MODULE_NAME = "elements"
ROUTES_MODULE_NAME = "routes"
CHASSIS_MODULE_NAME = "chassis"
SERVICES_MODULE_NAME = "services"


#
# MAVEN COMMAND HELPERS 
#

def get_camel_case_name(hyphenated_name):
    rv = ""
    for e in hyphenated_name.split("-"):
      rv = rv + e.title()
    return rv
    
def get_namespace_from_camel_case_name(camel_case_name):
    return camel_case_name[0].lower() + camel_case_name[1:]

def get_archetype_group_id(module_name):
    return ARCHETYPE_GROUP_ID_BASE + "." + module_name

def get_group_id(module_name):
    return GROUP_ID_BASE + "." + module_name

def get_subprocess_cmd_list(module_name, archetype_artifact_id, artifact_id):
    return [
                MAVEN_COMMAND,
                MAVEN_GOAL,
                ARCHETYPE_GROUP_ID_OPTION + get_archetype_group_id(module_name),
                ARCHETYPE_VERSION_OPTION + ARCHETYPE_VERSION,
                ARCHETYPE_ARTIFACT_ID_OPTION + archetype_artifact_id,
                GROUP_ID_OPTION + get_group_id(module_name),
                VERSION_OPTION + VERSION,
                ARTIFACT_ID_OPTION + artifact_id,
                ARTIFACT_ID_CAMEL_CASE_OPTION + get_camel_case_name(artifact_id),
                ARTIFACT_ID_NAMESPACE_OPTION + get_namespace_from_camel_case_name(get_camel_case_name(artifact_id)),
                ARTIFACT_SUBPACKAGE_NAME_OPTION + ELEMENTS_MODULE_NAME,
                INTERACTIVE_MODE_OPTION_AS_FALSE
            ]


#
# FUNCTIONS THAT HANDLE USER REQUESTS
#

# this is a bit of a hack to deal with the fact that maven, when it adds stuff to a pom file automatically, has a nasty
# habbit of adding new spaces and new line characters where they don't belong. this will remove any line that's 
# comprised of only string.whitespace characters (tab, linefeed, carriage return, space, etc) 
def clean_clrf_from_pom():
    printable_set = set(list(string.ascii_letters) + list(string.digits) + list(string.punctuation))
    with fileinput.input(files='pom.xml', inplace=True) as f:
    
        try:
            for line in f:            
                line_set = set(line)
                if len(printable_set) == len(printable_set - line_set):
                    pass
                else:
                    print(line, end='')
        except Exception as e:
            # if we don't do this the backup that fileinput.input() generated will get cleaned up on file close
            logger.error("something went wrong processing the pom file {}".format(e))
            logger.error("restoring original pom.xml file...")
            shutil.copyfile('pom.xml.bak', 'pom.xml')


def bean(artifact_id):
    logger.info("generating a bean named: {}".format(artifact_id))    
    os.chdir("./" + ELEMENTS_MODULE_NAME)
    
    rc = 0
    try:
        # purposefully NOT setting shell=True
        # https://docs.python.org/3/library/subprocess.html#security-considerations
        response = subprocess.run(
            get_subprocess_cmd_list(ELEMENTS_MODULE_NAME, "bean-archetype", artifact_id), 
            stdout=PIPE, 
            stderr=STDOUT
        )
        
        rc = response.returncode
        logger.debug(response.stdout.decode())
        if rc != 0:
          raise RuntimeError
    except RuntimeError:
        logger.error("something went wrong making the bean")
        logger.error("command attempted was: {}".format(response.args))
        logger.error("returncode was: {}\n".format(response.returncode)) 
        logger.error("output was:\n**********\n{}**********\n".format(response.stdout.decode()))
    
    
    logger.info("cleaning whitespace maven injected into pom file (maven has a bug)")
    clean_clrf_from_pom()
    os.chdir("../")
    
    if rc == 0:
        logger.info("done!")
    else:
        logger.warn("something went wrong - exiting with non zero return code: {}".format(rc))
        
    return rc 


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
            logger.error("\n** input needs to be a number and one that's listed in the menu! **\n".format(raw_choice))


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

