public class EditableList
{
  private DLLNode head;   // points to the dummy head node
  private DLLNode tail;   // points to the dummy tail node
  private int n;          // number of actual items

  private DLLNode cursor; // points to the current item
  private int current;    // position of the current item

  public EditableList()
  {
    head = new DLLNode( "if you see this, you are doomed" ); 
    tail = new DLLNode( "Huh???" );
    
    cursor =  new DLLNode( "" );
    n = 1;
    
    current = 0;

    head.next = cursor;
    cursor.prev = head;
    cursor.next = tail;
    tail.prev = cursor;
  }

  // insert a new item 
  // immediately after the
  // current item
  public void insert( String s )
  {
    add(current, s);
    
  }
  public void add ( String s) {
	  DLLNode temp = new DLLNode( s );
	  
	/*  temp.next = cursor.next;
	  temp.prev = cursor;
	  
	  cursor.next.prev = temp;
	  cursor.next = temp; */
	  
	DLLNode oldLast = tail.prev;
	  
	  
	  
	  temp.prev = oldLast;
	  oldLast.next = temp; 
	   
	   
	  
	    
	  
	  
	  tail.prev = temp;
	  temp.next = tail; 
	  n += 1;
  }
  public void add ( int offset, String s ) {
	  DLLNode temp = new DLLNode( s );
	  DLLNode off = head.next;
	  for (int i = 0; i < offset; i++) {
		  if (off == tail) { break; }
		  off = off.next;
	  }
	  
	  if (off == tail) {
		  add(s);
		  return;
	  }
	  
	  DLLNode oldPrev = off.prev;
	  oldPrev.next = temp;
	  temp.prev = oldPrev;
	  
	  temp.next = off;
	  off.prev = temp;
	  
	  n += 1;
	  
  }
  // remove the current item
  // (if only one real item, refuse)
  public void remove()
  {
	  if (n == 1) { return; }
	  
	  cursor.prev.next = cursor.next;
	  cursor.next.prev = cursor.prev;
	  n -= 1;
  }
  
  public void remove(int offset) {
	  if (n == 0) { 
		  return;
	  }
	  
	  DLLNode temp = head.next;
	  for (int i = 0; i < offset; i++) {
		  if (temp.next == tail) { 
			  break;
		  }
		  temp = temp.next;
	  }
	  temp.next.prev = temp.prev;
	  temp.prev.next = temp.next;
	  n -= 1;
  }
  // return the data item stored
  // in the node offset hops from
  // the cursor node, if offset is
  // valid, otherwise return null
  public String get( int offset ) {
	  DLLNode get = head.next;
	  for(int i = 0; i < offset; i++) {
		  if (get.next == tail) { 
			  break; 
		  }
		  get = get.next;
	  }
	  return get.data;
  }

  // replace the current data by s
  public void set( String s )
  {
	  if (cursor == head || cursor == tail) {
		  System.out.println("CURSOR ON HEAD OR TAIL!!! BIG PROBLEM!!!");
		  return;
	  }
     cursor.data = s;
  }
  public void set ( int offset, String s ) {
	  DLLNode temp = head.next;
	  for (int i = 0; i < offset; i++) {
		  if (temp.next == tail) 
		  { 
			  break;
		  }
		  temp = temp.next;
	  }
	  temp.data = s;
  }
  // if not on first real node,
  // move prev and return true,
  // else don't move and return false
  public boolean back()
  {
    if( cursor.prev != head )
    {
      cursor = cursor.prev;
      current--;
      return true;
    }
    else
      return false;
  }

  // if not on last real node,
  // move next and return true,
  // else don't move and return false
  public boolean forward()
  {
	  if (cursor == tail) { return false; }
	  cursor = cursor.next;
	  current++;
	  return true;
  }
  
  public int size()
  {
    return n;
  }

  public int current()
  {
    return current;
  }
  
  // make first actual item current
  public void moveToFirst()
  {
	  cursor = head.next;
  }

  // make last actual item current
  public void moveToLast()
  {
	  cursor = tail;
  }

}