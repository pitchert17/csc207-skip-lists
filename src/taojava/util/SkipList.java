package taojava.util;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Random;
import java.io.PrintWriter;

/**
 * A randomized implementation of sorted lists.  
 * 
 * @author Samuel A. Rebelsky
 * @author Tommy Pitcher
 * @author Charlie Gao
 * Referenced
 * http://en.wikipedia.org/wiki/Skip_list
 * Zhi Chen and Leah Greenberg
 * Zoe Wolter
 * 
 */
public class SkipList<T extends Comparable<T>>
    implements SortedList<T>
{
  // +--------+----------------------------------------------------------
  // | Fields |
  // +--------+
  //maximum amount of levels
  int maxLevel = 20;
  //the level we are currently at
  int currentLevel;
  //counts the number of modifications
  int mods;
  //probablilty of a new level
  double probability = .5;

  Node<T> dummy;// = new Node(null, (Node[]) new Object[20]);

  // +------------------+------------------------------------------------
  // | Internal Classes |
  // +------------------+

  /**
   * Nodes for skip lists.
   */
  public class Node<T>
  {
    // +--------+--------------------------------------------------------
    // | Fields |
    // +--------+

    /**
     * An array of pointers to the following nodes
     */
    public Node<T>[] nexts;
    /**
     * The value in stored in the node
     */
    public T val;

    //Constructors
    public Node(T val, int level)
    {
      this.val = val;
      this.nexts = new Node[level + 1];
    }//Node
  } // class Node

  // +--------------+----------------------------------------------------
  // | Constructors |
  // +--------------+

  public SkipList()
  {
    this.dummy = new Node(null, maxLevel);
    this.currentLevel = 0;
  }//SkipList

  // +-------------------------+-----------------------------------------
  // | Internal Helper Methods |
  // +-------------------------+
  public int newLevel()
  {
    int level = 1;
    Random rand = new Random();
    while (rand.nextDouble() < this.probability)
      {
        level++;
      }//while()
    return Integer.min(level, this.maxLevel);
  }//randomLevel()
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
    return new Iterator<T>()
      {
        Node<T> cursor = SkipList.this.dummy;

        int mods = SkipList.this.mods;

        void failFast()//from the LinkedListLab
        {
          if (this.mods != SkipList.this.mods)
            {
              throw new ConcurrentModificationException();
            }//if
        }//failFast()

        public boolean hasNext()
        {
          failFast();
          return (cursor.nexts[1] != null);
        }//hasNext()

        public T next()
        {
          failFast();

          if (!this.hasNext())
            {
              throw new NoSuchElementException();
            }//if
          this.cursor = this.cursor.nexts[1];

          return this.cursor.val;
        }//next()

      };//Iterator<T>
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

    Node<T> current = this.dummy;
    Node<T>[] update = new Node[maxLevel + 1];

    for (int i = currentLevel; i >= 0; i--)
      {
        while ((current.nexts[i] != null)
               && current.nexts[i].val.compareTo(val) < 0)
          {
            current = current.nexts[i];
          }//while
        update[i] = current;
      }//for

    current = current.nexts[0];

    if ((current == null) || (!current.val.equals(val)))
      {
        int newLevel = newLevel();

        if (newLevel > currentLevel)
          {
            for (int i = currentLevel + 1; i <= newLevel; i++)
              {
                update[i] = this.dummy;
              }//for

            currentLevel = newLevel;
          }//if

        current = new Node(val, newLevel);

        for (int i = 0; i <= currentLevel; i++)
          {
            current.nexts[i] = update[i].nexts[i];
            update[i].nexts[i] = current;
          }//for
      }//if
    mods++;
  }//add(T val)

  /*public void add(T val)
  {

    //check if this is the first node to be added by accessing dummy
    if (dummy.nexts[1] == null)
      { //nothing has been added, so nexts of dummy points to null
        for (Node n : dummy.nexts)
          {
            Node[] nexts = (Node[]) new Object[20];
            Node newnode = new Node(val, nexts);
            n = newnode;
          }
      }
    else
      { //all other nodes
        Node[] nexts = (Node[]) new Object[20];
        Node newnode = new Node(val, nexts);

        Random rand = new Random();
        //if the new element would appear at the front of the list
        if (dummy.nexts[0].val.compareTo(val) <= 0)
          {// if val is smaller than the first element
            Node formerFirst = dummy.nexts[0]; //store the former first node
            for (int i = 0; i >= nexts.length - 1; i++)
              {
                dummy.nexts[i] = newnode; //set all dummy node nexts to newnode
              }//for

            // now we set the pointers of the formerfirst correctly.
            for (int i = 0; i < nexts.length + 1; i++)
              {
                double probabilityRange = (1 / (2 ^ i));
                double randomNumber = rand.nextDouble();

                if (randomNumber < probabilityRange)
                  {//add the node at the level
                    newnode.nexts[i] = formerFirst;
                  }
                else
                  {
                    newnode.nexts[i] = formerFirst.nexts[i];
                    formerFirst.nexts[i] = null;
                  }
              }

          }//if

        //if the new element would not be first
        Node current = dummy.nexts[0]; // set current "iterator" node as the first element
        for (int i = nexts.length - 1; i >= 0; i--)//iterate through the levels
          {
            levelLoop: while (current.nexts[i] != null)
              { //iterate through a level
                if (current.nexts[i].val.compareTo(val) >= 0)
                  { //if next value is greater than val...
                    if (i > 0)
                      { //... terminate loop, move down a level (current remains at current node)
                        break levelLoop;
                      }
                    else
                      { //... add val to the skiplist (level is zero, so it must be here)
                        for (int j = 0; j < nexts.length + 1; j++)
                          { //iterate through the levels
                            double probabilityRange = (1 / (2 ^ j));
                            double randomNumber = rand.nextDouble();

                            if (randomNumber < probabilityRange)
                              {//add the node at the level
                                Node nodeSearch = dummy.nexts[j];
                                boolean added = false;
                                searchForPosition: while (nodeSearch.nexts[j] != null)
                                  {
                                    if (nodeSearch.nexts[j].val.compareTo(val) >= 0)
                                      {//if the next value at a level is greater, insert
                                        Node nextNode = nodeSearch.nexts[j];
                                        nodeSearch.nexts[j] = newnode;
                                        newnode.nexts[j] = nextNode;
                                        added = true;
                                        break searchForPosition;
                                      }
                                    nodeSearch = nodeSearch.nexts[j]; //advance nodeSearch
                                  }

                                if (!added)
                                  { //if val is the greatest at its level, add.
                                    nodeSearch.nexts[j] = newnode;
                                  }
                              }
                          }
                      }
                  }
                //advance one node
                current = current.nexts[i];
              }
          }//for

      }
  } // add(T val)
  */
  /**
   * Determine if the set contains a particular value.
   */
  public boolean contains(T val)
  {
    Node<T> current = this.dummy;
    //iterate through the levels
    for (int i = this.maxLevel - 1; i >= 0; i--)
      {
        while ((current.nexts[i] != null)
               && current.nexts[i].val.compareTo(val) < 0)
          {
            //advance nodes
            current = current.nexts[i];
          }//while
      }//for

    current = current.nexts[0];//go the the bottom level

    if (current.val.equals(val))
      {
        //if the value is there return true
        return true;
      }//if
    else
      { //if it isn't return false
        return false;
      }//else
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
    Node<T> current = this.dummy;
    Node<T>[] update = new Node[maxLevel + 1];
    //iterate through and set update
    for (int i = currentLevel; i >= 0; i--)
      {
        while ((current.nexts[i] != null)
               && (current.nexts[i].val.compareTo(val) < 0))
          {
            current = current.nexts[i];
          }//while()
        update[i] = current;
      }//for()
    current = current.nexts[0];
    if (current.equals(null))
      {
        return;
      }//if()
    else if (current.val.equals(val))
      {

        for (int i = 0; i <= currentLevel; i++)
          {
            if (update[i].nexts[i] != current)
              {
                break;
              }//if()
            update[i].nexts[i] = current.nexts[i];
          }//for()
           //decrement the level of the list
        while ((currentLevel > 0) && (this.dummy.nexts[currentLevel] == null))
          {
            currentLevel--;
          }//while()
           //we have made a modification, so we increment mods
        mods++;
      }//else if()
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
