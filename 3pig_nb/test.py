from guineapig import *
import re
import math

# supporting routines can go here
def flat1(line):
    for i in xrange(0, len(line), 2):
        yield (line[i])

def flat2(line):
    for i in xrange(1, len(line), 2):
        yield (line[i])


#always subclass Planner
class Test(Planner):
    # params is a dictionary of params given on the command line.
    doc = ReadLines('train.txt') | Map(by = lambda line:line.strip().split())
    t1 = Flatten(doc, by=flat1)
    t2 = Flatten(doc, by=flat2)


    output = Map(joinedDoc, by=classify)

# always end like this
if __name__ == "__main__":
    NB().main(sys.argv)

# supporting routines can go here
