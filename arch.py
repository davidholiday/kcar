#! /usr/bin/env python3


'''

'''


import argparse
import os


THING_TYPES = [
    "bean", 
    "processor", 
    "connector (NOT YET IMPLEMENTED)",
    "route (NOT YET IMPLEMENTED)", 
    "chassis (NOT YET IMPLEMENTED)", 
    "service (NOT YET IMPLEMENTED)",
    "raw camel archetype (NOT YET IMPLEMENTED)"
]

TYPE_COUNT = len(THING_TYPES)

MENU_DICT = {
    i: THING_TYPES[i]
    for i 
    in range(0, TYPE_COUNT)
}



def print_menu():
    print("-= What kind of thing would you like to make? =-")
    for i in range(0, TYPE_COUNT):
        print("{}.  {}".format(str(i), MENU_DICT[i]))  
    
    print("----")


def main(args):
    ai = args.artifact_id 
    

    done = False
    while not done:
        try:
            done = True
            print_menu()
            choice = input("enter choice or ctrl-c to exit >> ")
            choice = int(choice)
            if choice not in MENU_DICT.keys():
                print("\n{} is not a valid menu selection!".format(choice))

        except KeyboardInterrupt:
            print("\nbye!")
        except ValueError:
            done = False
            print("\n{} is not a valid menu selection!".format(choice))

             
    
    

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

