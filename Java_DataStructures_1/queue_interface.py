# Works with Python 3.4+

# pip install python-interface
from interface import Interface, implements

class IQueue(Interface):
    """Defines the methods for a FIFO queue.
    [for implementation use "class ClassName(implements(IQueue)):]"
    """

    def isEmpty(self) -> bool:
        """Checks whether the queue is empty and returns true if it is"""
        pass

    def put(self, item: int):
        """Insert an item to the queue"""
        pass
    
    def get(self) -> int:
        """Remove and return the oldest item of the queue
        Exceptions
            NoSuchElementException if the queue is empty
        """
        pass

    def peek(self) -> int:
        """Returns (without removing) the oldest item of the queue

        Exceptions
            NoSuchElementException if the queue is empty
        """
        pass

    
    def printQueue(self, stream):
        """Print the elements of the queue, starting from the oldest item, to the print stream given as argument. For example, to print the elements to the standard output pass stdout as parameter.
        
        Parameters:
            stream (TextIO): The stream to write to
        
        E.g.
            from sys import stdout
            printQueue(stdout);
        """
        pass

    def size(self) -> int:
        """Returns the size of the queue, 0 if it is empty"""
        pass
