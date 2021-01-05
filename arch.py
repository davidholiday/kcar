#! /usr/bin/env python3


'''

'''


import argparse
import os




def bean(artifact_id):
    print("artifact_id is: {}".format(artifact_id))



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
            print("\nbye!")
        except ValueError:
            done = False
            print("\n** input needs to be a number and one that's listed in the menu! **\n".format(raw_choice))

    MENU_DICT.get(choice_int)
    
    
     
if __name__ == "__main__":
    description_text = "helper to create starter code for Java Service Factory elements, routes, chassis, and services."

    parser = argparse.ArgumentParser(description=description_text)
    
    parser.add_argument(
        "artifact_id", 
        help="name of the thing you want to make. if name is more than one word, use hyphens eg 'health-check-route' ", 
        type=str
    )

    args = parser.parse_args()
    main(args)

