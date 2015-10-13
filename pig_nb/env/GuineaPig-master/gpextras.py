##############################################################################
# (C) Copyright 2014, 2015 William W. Cohen.  All rights reserved.
##############################################################################

from guineapig import *

class ReadCSV(Reader):
    """ Returns the lines in a CSV file, converted to Python tuples."""

    def __init__(self,src,**kw):
        Reader.__init__(self,src)
        self.kw = kw

    def rowGenerator(self):
        for tup in csv.reader(sys.stdin,**self.kw):
            yield tup

    def __str__(self):
        return 'ReadCVS("%s",%s)' % (self.src,str(self.kw)) + self.showExtras()

class ReadBlocks(Reader):
    """ Returns blocks of non-empty lines, separated by empty lines"""

    def __init__(self,src,isEndBlock=lambda line:line=="\n"):
        Reader.__init__(self,src)
        self.isEndBlock = isEndBlock

    def rowGenerator(self):
        buf = []
        for line in sys.stdin:
            if self.isEndBlock(line):
                yield buf
                buf = []
            else:
                buf.append(line)
        if buf:
            yield buf

    def __str__(self):
        return 'ReadBlocks("%s")' % self.src + self.showExtras()

class Log(ReplaceEach):
    """Print logging messages to stderr as data is processed. 
    For every row, the logfun will be called with arguments
    logfun(rowValue,rowIndex).
    """

    def __init__(self, inner=None, logfun=lambda rowV,rowI:None):
        self.rowNum = 0
        def logfunCaller(rowValue):
            self.rowValue += 1
            self.logfun(rowValue,self.rowNum)
            return rowValue
        ReplaceEach.__init__(self,inner,by=logfunCaller)
        
    def __str__(self):
        return 'Log("%s")' % self.src + self.showExtras()

class LogEchoFirst(Log):

    """Echo the first N things."""
    
    def __init__(self, inner=None, first=10):
        def logfirst(rowValue,rowIndex):
            if rowIndex<=first:
                print >> sys.stderr, 'row %d: "%s"' % (rowIndex,rowValue)
        Log.__init__(self, inner=inner, logfun=logfirst)

class LogProgress(Log):

    """Echo a status message every 'interval' rows."""
    
    def __init__(self, inner=None, msg="Logging progress", interval=1000):
        def logprogress(rowValue,rowIndex):
            if (rowIndex % interval)==0:
                print >> sys.stderr, "%s: %d rows done" % (msg,rowIndex)
        Log.__init__(self, inner=inner, logfun=logprogress)
