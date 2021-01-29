# Works with Python 3.4+

# pip install python-interface
from interface import Interface, implements

class IStack(Interface):
    """Defines the methods for a Stack
    [for implementation use "class ClassName(implements(IStack)):]"""
    
    def isEmpty(self) -> bool:
        """Checks whether the stack is empty and returns true if it is"""
        pass

    def push(self, item: str):
        """Insert an item to the stack
        Parameters:
            item: The item to add"""
        pass
    
    def pop(self) -> str:
        """Returns the item on the top of the stack
        
        Exceptions
            NoSuchElementException if the stack is empty"""
        pass

    def peek(self) -> str:
        """Returns (without removing) the top item of the stack

        Exceptions
            NoSuchElementException if the stack is empty
        """
        pass

    def printStack(self, stream):
        """Print the elements of the stacj, starting from the top item, to the print stream given as argument. For example, to print the elements to the standard output pass stdout as parameter.
        
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
