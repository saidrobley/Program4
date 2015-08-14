import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.util.*;
import java.io.*;

public class Ex4pre extends Basic
{
  public static void main(String[] args)
  {
    // example: hard-coded location and size of window:
    Ex4pre a = new Ex4pre("Simple Text Editor", 0, 0, 900, 600);

  }
 
  private ArrayList<String> doc;  // the list of all the strings in the document
  private int cursorRow; // index in the list of cursor
  private int cursorCol; // column in doc.get(cursor)

  // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

  public Ex4pre( String title, int ulx, int uly, int pw, int ph )
  {
    super(title,ulx,uly,pw,ph);

    doc = new ArrayList<String>();

    String fileName = FileBrowser.chooseFile( true );

    try{
      Scanner input = new Scanner( new File( fileName ) );

      while( input.hasNext() )
      {
        String s = input.nextLine();
        doc.add( s );
      }
    }
    catch(Exception e)
    {
      System.out.println("something went wrong");
      e.printStackTrace();
      System.exit(1);
    }

    setBackgroundColor( new Color( 0, 0, 0 ) );
    cameras.add( new Camera( 10, 30, 
                             800, 500, 
                             0, 50, 0, 50,
                             Color.white ) );
 
    cursorRow = 0;  cursorCol = 0;

    super.start();
  }

  private static double leftMargin = 5;
  private static double lineHeight = 1.8;
  private static double symWidth = 0.8;

  public void step()
  {
    Camera cam;

    cam = cameras.get(0);
    cam.activate();
    // make the camera use the desired font
    cameras.get(0).setFont( new Font( Font.MONOSPACED, Font.PLAIN, 18 ) );

    // draw 12 lines before and after the cursorRow
    double y = 48;
    for( int k=-12; k<=12; k++ )
    {
      if( 0<=cursorRow+k && cursorRow+k < doc.size() )
      {
        // draw line number
        cam.setColor( Color.green );
        cam.drawText( (cursorRow+k) + ": ", 0, y );

        cam.setColor( Color.black );
        String line = doc.get( cursorRow + k);
        // draw line one symbol at a time
        for( int j=0; j<line.length(); j++ )
          cam.drawText( ""+line.charAt(j), leftMargin+symWidth*j, y );

        if( k==0 )
        {
          // draw the vertical cursor
          if( getStepNumber() % 4 == 0 )
            cam.setColor( Color.blue );
          else
            cam.setColor( Color.red );
          double x = leftMargin + cursorCol*symWidth;
          cam.fillRect( x-0.2, y-0.5, 0.2, 2.25 );
        }

      }// cursorRow+k is valid

      y -= lineHeight;

    }// draw 25 lines

  }// step method

  public void keyTyped( KeyEvent e )
  {
    char key = e.getKeyChar();

System.out.println("key typed coerced to int: " + ((int) key) );

    if( ' '<=key && key<='~' )
    {// insert printable symbol at the cursor
      String s = doc.get( cursorRow );
      if( cursorCol==0 )
        s = "" + key;
      else
        s = s.substring( 0, cursorCol ) + key + s.substring( cursorCol );
      doc.set( cursorRow, s );
      cursorCol++;
    }
    else if( key == 'q' - 'a' + 1 )
    {// ctrl-q --- save and quit
      save();
      System.exit(0);
    }
    else if( key == 'f' - 'a' + 1 )
    {// ctrl-f   --- put column cursor on position 0 of current line
      cursorCol = 0;
    }
    else if( key == 'l' - 'a' + 1 )
    {// ctrl-l   --- put column cursor on last position of current line
      cursorCol = doc.get( cursorRow ).length();
    }
    else if( key == 'd' - 'a' + 1 )
    {// ctrl-d   --- delete entire current line
      if( doc.size() > 1 )
      {
        doc.remove( cursorRow );
        if( cursorRow >= doc.size() )
        {
          cursorRow = doc.size()-1;
        }
      }
    }
    else if( key == 'e' - 'a' + 1 )
    {// ctrl-e  --- erase current line
      doc.set( cursorRow, "" );
    }
    else if( key == 'c' - 'a' + 1 )
    {// ctrl-c   --- combine current line and next, if there is a next line
      if( doc.size() > 1 && cursorRow < doc.size()-1 )
      {
        String s1 = doc.get(cursorRow);
        String s2 = doc.get(cursorRow+1);
        doc.set( cursorRow, s1+s2 );
        doc.remove( cursorRow+1 );
      }
    }

    // always make sure changes didn't make cursorCol too far over
    cursorCol = Math.min( cursorCol, doc.get(cursorRow).length() );
  }

  public void keyPressed( KeyEvent e )
  {
    int code = e.getKeyCode();
 
System.out.println("key pressed: " + code );

    if( code == KeyEvent.VK_DOWN && cursorRow < doc.size()-1 )
    {
      cursorRow++;
    }
    else if( code == KeyEvent.VK_UP && cursorRow > 0)
    {
      cursorRow--;
    }
    else if( code == KeyEvent.VK_LEFT )
    {
      if( cursorCol > 0 )
      cursorCol--;
    }
    else if( code == KeyEvent.VK_RIGHT )
    {
      if( cursorCol < doc.get( cursorRow ).length() )
        cursorCol++;
    }
    else if( code == KeyEvent.VK_DELETE ) 
    {
      String s = doc.get(cursorRow);
      if( cursorCol < s.length() )
      {
        s = s.substring(0,cursorCol) + s.substring(cursorCol+1);
        doc.set( cursorRow, s );
      }
      else if( s.length() == 0 )
      {// kill the empty line
        doc.remove( cursorRow );
      }
    }
    else if( code == KeyEvent.VK_BACK_SPACE )
    {
      String s = doc.get(cursorRow);
      if( cursorCol == 0 && cursorRow > 0 )
      {// join the line above with this one
        cursorCol = doc.get( cursorRow-1 ).length();
        doc.set( cursorRow-1, doc.get( cursorRow-1 ) + doc.get( cursorRow ) );
        doc.remove( cursorRow );
        cursorRow--;
      }
      else if( cursorCol > 0 )
      {
        s = s.substring(0,cursorCol-1) + s.substring(cursorCol);
        doc.set( cursorRow, s );
        cursorCol--;
      }
      else if( s.length() == 0 )
      {// kill the empty line
        doc.remove( cursorRow );
      }
    }
    else if( code == KeyEvent.VK_ENTER )
    {
      String s = doc.get(cursorRow);
      String first = s.substring( 0, cursorCol );
      String second = s.substring( cursorCol );
      doc.set( cursorRow, first );
      doc.add( cursorRow+1, second );
      cursorRow++;
      cursorCol = 0;
    }

    // always make sure changes didn't make cursorCol too far over
    cursorCol = Math.min( cursorCol, doc.get(cursorRow).length() );

  }

  private void save()
  {
    String fileName = FileBrowser.chooseFile( false );
    try{
      PrintWriter output = new PrintWriter( new File( fileName ) );
      for( int k=0; k<doc.size(); k++ )
      {
        output.println( doc.get( k ) );
      }
      output.close();
    }
    catch(Exception e)
    {
      System.out.println("Something went wrong with saving.  Sorry.");
    }
  }

}