package taojava.util;

import java.util.Iterator;
import java.util.Arrays;

/**
 * A randomized implementation of sorted lists.  
 * 
 * @author Samuel A. Rebelsky
 * @author Tommy Pitcher
 * @author Charlie Gao
 */
public class SkipList<T extends Comparable<T>>
    implements SortedList<T>
{
  // +--------+----------------------------------------------------------
  // | Fields |
  // +--------+
  Node dummy = new Node(null, (Node[]) new Object[4]);

  // +------------------+------------------------------------------------
  // | Internal Classes |
  // +------------------+

  /**
   * Nodes for skip lists.
   */
  public class Node
  {
    // +--------+--------------------------------------------------------
    // | Fields |
    // +--------+

    /**
     * The value stored in the node.
     */
    Node[] nexts;

    T val;

    //Constructors
    public Node(T val, Node[] nexts)
    {
      this.val = val;
      this.nexts = nexts;

    }//Node
  } // class Node

  // +--------------+----------------------------------------------------
  // | Constructors |
  // +--------------+

  // +-------------------------+-----------------------------------------
  // | Internal Helper Methods |
  // +-------------------------+

  // +-----------------------+-------------------------------------------
  // | Methods from Iterable |
  // +-----------------------+

  /**
   * Return a read-only iterator (one that does not implement the remove
   * method) that iterates the values of the list from smallest to
   * largest.
   */
  public Iterator<T> iterator()
  {
    // STUB
    return null;
  } // iterator()

  // +------------------------+------------------------------------------
  // | Methods from SimpleSet |
  // +------------------------+

  /**
   * Add a value to the set.
   *
   * @post contains(val)
   * @post For all lav != val, if contains(lav) held before the call
   *   to add, contains(lav) continues to hold.
   */
  public void add(T val)
  {

    //check if this is the first node to be added by accessing dummy
    if (dummy.nexts[1] == null)
      { //nothing has been added, so nexts of dummy points to null
        for (Node n : dummy.nexts)
          {
            Node[] nexts = (Node[]) new Object[4];
            Node newnode = new Node(val, nexts);
            n = newnode;
          }
      }
    else
      { //all other nodes
        Node[] nexts = (Node[]) new Object[4];
        Node newnode = new Node(val, nexts);
        if (dummy.nexts[1].val.compareTo(val) <= 0)
          {
            for (int i = 0; i >= nexts.length -1; i++)
              {
                dummy.nexts[i] = newnode;
              }//for
          }//if
        for (int i = 1; i <= nexts.length - 1;  i++)//iterate through the levels
          {
            if (dummy.nexts[i].val.compareTo(val) >= 0)
              {
                while (dummy.nexts[i] != null)//iterate through all Nodes at level i
                  {
                    if(dummy.nexts[i].val.compareTo(val) <= 0)//go down a level
                      {
                        
                      }//if
                    else  
                  }//while
                
                
            
              }//if
          }//for

      }
  } // add(T val)

  /**
   * Determine if the set contains a particular value.
   */
  public boolean contains(T val)
  {
    return false;
  } // contains(T)

  /**
   * Remove an element from the set.
   *
   * @post !contains(val)
   * @post For all lav != val, if contains(lav) held before the call
   *   to remove, contains(lav) continues to hold.
   */
  public void remove(T val)
  {
    // STUB
  } // remove(T)

  // +--------------------------+----------------------------------------
  // | Methods from SemiIndexed |
  // +--------------------------+

  /**
   * Get the element at index i.
   *
   * @throws IndexOutOfBoundsException
   *   if the index is out of range (index < 0 || index >= length)
   */
  public T get(int i)
  {
    // STUB
    return null;
  } // get(int)

  /**
   * Determine the number of elements in the collection.
   */
  public int length()
  {
    // STUB
    return 0;
  } // length()

} // class SkipList<T>
