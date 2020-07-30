import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;
import java.io.*; 
import java.awt.event.*;
import java.beans.PropertyChangeListener;
import javax.swing.plaf.metal.*; 
import javax.swing.text.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import java.net.MalformedURLException;
import java.nio.file.Path; 
import java.nio.file.Paths; 

class editor extends JFrame implements ActionListener
{
    JFrame frame;
    JTextArea textArea;
    JScrollPane scrollPane;
    editor()
    {
        JPanel panel1=new JPanel();
        JPanel panel2=new JPanel();
        JPanel panel3=new JPanel();
        JButton findButton=new JButton("Find");
        JLabel findLabel=new JLabel("Enter text to Search: ");
        JTextField findTextField=new JTextField(20);
        JLabel replaceLabel=new JLabel("Enter text to Replace with searched word: ");
        JTextField replacTextField=new JTextField(20);
        JButton replaceButton=new JButton("Replace");
        JLabel compileLabel=new JLabel("Enter Class(which contains main) name ");
        JTextField classTextField=new JTextField(20);
        JButton compileButton=new JButton("Compile");

        panel1.add(findLabel);
        panel1.add(findTextField);
        panel1.add(findButton);

        panel2.add(replaceLabel);
        panel2.add(replacTextField);
        panel2.add(replaceButton);

        panel3.add(compileLabel);
        panel3.add(classTextField);
        panel3.add(compileButton);

        textArea=new JTextArea(35,67);
        textArea.getBorder();
        JMenuBar menuBar=new JMenuBar();
        scrollPane=new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JMenu fileMenu=new JMenu("Files");

        JMenuItem File_item_1=new JMenuItem("New");
        JMenuItem File_item_2=new JMenuItem("Open");
        JMenuItem File_item_3=new JMenuItem("Save");

        File_item_1.addActionListener(this);
        File_item_2.addActionListener(this);
        File_item_3.addActionListener(this);

        fileMenu.add(File_item_1);
        fileMenu.add(File_item_2);
        fileMenu.add(File_item_3);

        menuBar.add(fileMenu);

        JMenu toolMenu=new JMenu("Tools");

        JMenuItem toolMenu_Item_1=new JMenuItem("Cut");
        JMenuItem toolMenu_Item_2=new JMenuItem("Copy");
        JMenuItem toolMenu_Item_3=new JMenuItem("Paste");

        toolMenu_Item_1.addActionListener(this);
        toolMenu_Item_2.addActionListener(this);
        toolMenu_Item_3.addActionListener(this);

        findButton.addActionListener(new ActionListener(){

            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                textArea.getHighlighter().removeAllHighlights();
                //System.out.println(findTextField.getText());
                String pat=findTextField.getText().toString();
                String txt=textArea.getText().toString();
                Highlighter highlighter=textArea.getHighlighter();
                HighlightPainter painter=new DefaultHighlighter.DefaultHighlightPainter(Color.RED);
                // Rabin-Karp's Algorithm of String Matching.
                int d=256;
                int M = pat.length(); 
                int N = txt.length(); 
                int i, j; 
                int q=101;
                int p = 0; 
                int t = 0; 
                int h = 1; 
                for (i = 0; i < M-1; i++) 
                    h = (h*d)%q; 
                for (i = 0; i < M; i++) 
                { 
                    p = (d*p + pat.charAt(i))%q; 
                    t = (d*t + txt.charAt(i))%q; 
                }
                for (i = 0; i <= N - M; i++)
                {
                    if ( p == t )
                    {
                        for (j = 0; j < M; j++) 
                        {
                            if (txt.charAt(i+j) != pat.charAt(j))
                            {
                                break;
                            }
                        }
                        if (j == M) 
                        {
                            try
                            {

                                highlighter.addHighlight(i, i+pat.length(), painter);
                            }
                            catch(Exception exception)
                            {

                            }
                        }
                    }
                    if ( i < N-M ) 
                    {
                        t = (d*(t - txt.charAt(i)*h) + txt.charAt(i+M))%q; 
                        if (t < 0) 
                        {
                            t = (t + q); 
                        }
                    }
                } 
            }
        });
        replaceButton.addActionListener(new ActionListener(){
        
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                String replaceString=replacTextField.getText().toString();
                String onTextArea=textArea.getText().toString();
                String newText=onTextArea.replaceAll(findTextField.getText(),replaceString);
                textArea.setText(newText);
            }
        });
        // Code Compilation
        compileButton.addActionListener(new ActionListener() {
        
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String getClassName=classTextField.getText();
                //JOptionPane.showMessageDialog(frame,getClassName);
                JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
                DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
                StringWriter writer = new StringWriter(); 
                PrintWriter out = new PrintWriter(writer);

                String[] line=textArea.getText().split("\\n");
                // for(int l=0;l<line.length;l++)
                // {
                //     line[l]=line[l].replaceAll("\"", "\\\\\"");
                //     System.out.println(line[l]);
                // }
                for(int g=0;g<line.length;g++)
                {
                    out.println(line[g]);
                }
                out.close();

                JavaFileObject file = new JavaSourceFromString(getClassName, writer.toString());
                Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);
                CompilationTask task = compiler.getTask(null, null, diagnostics, null, null, compilationUnits);

                boolean success = task.call();
                String check="";
                for (Diagnostic diagnostic : diagnostics.getDiagnostics())
                {
                    System.out.println(diagnostic.getCode());
                    check=check+diagnostic.getCode()+"\n";
                    System.out.println(diagnostic.getKind());
                    check=check + diagnostic.getKind()+"\n";
                    // System.out.println(diagnostic.getPosition());
                    // check=check+diagnostic.getPosition()+"\n";
                    // System.out.println(diagnostic.getStartPosition());
                    // check=check+diagnostic.getStartPosition()+"\n";
                    // System.out.println(diagnostic.getEndPosition());
                    // check=check+diagnostic.getEndPosition()+"\n";
                    System.out.println(diagnostic.getSource());
                    check=check+diagnostic.getSource()+"\n";
                    System.out.println(diagnostic.getMessage(null));
                    check=check+diagnostic.getMessage(null)+"\n";
                }
                System.out.println("Success: " + success);
                if (success)
                {
                    try 
                    {
                        URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { new File("").toURI().toURL() });
                        getContentPane().setBackground(Color.BLACK);
                        JOptionPane.showMessageDialog(frame,"Compiled Succesfully");
                    } 
                    catch (MalformedURLException es)
                    {
                        System.err.println("Url not found" + es);
                    }
                }
                else
                {
                    getContentPane().setBackground(Color.LIGHT_GRAY);
                    JOptionPane.showMessageDialog(frame,check);
                }
            }
        });

        toolMenu.add(toolMenu_Item_1);
        toolMenu.add(toolMenu_Item_2);
        toolMenu.add(toolMenu_Item_3);

        menuBar.add(toolMenu);

        add(panel1);
        add(panel2);
        add(panel3);
        setJMenuBar(menuBar);
        setTitle("Text Editor for Java");
        setLayout(new FlowLayout());
        add(scrollPane,BorderLayout.EAST);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.BLACK);
        setSize(900,800);
        setVisible(true);

    }
    public void actionPerformed(ActionEvent e) 
	{
        String string=e.getActionCommand();
        if( string.equals("Cut"))
        {
            textArea.cut();
        }
        else if(string.equals("Copy"))
        {
            textArea.copy();
        }
        else if(string.equals("Paste"))
        {
            textArea.paste();
        }
        else if(string.equals("New"))
        {
            textArea.setText("");
        }
        else if(string.equals("Open"))
        {
            JFileChooser fileChooser=new JFileChooser("f:");
            int r=fileChooser.showOpenDialog(null);
            if(r==JFileChooser.APPROVE_OPTION)
            {
                File file=new File(fileChooser.getSelectedFile().getAbsolutePath());
                try
                {
                    String s1="",sl="";
                    FileReader fileReader=new FileReader(file);
                    BufferedReader br=new BufferedReader(fileReader);
                    sl=br.readLine();
                    while( (s1=br.readLine()) !=null)
                    {
                        sl=sl + "\n" + s1;
                    }
                    textArea.setText(sl);
                    
                } catch (Exception evt) 
                {
                    JOptionPane.showMessageDialog(frame,evt.getMessage());
                }
            }
            else
            {
                JOptionPane.showMessageDialog(frame, "Operation cancelled !");
            }
        }
        else if(string.equals("Save"))
        {
            JFileChooser fileChooser=new JFileChooser("f:");
            int r=fileChooser.showSaveDialog(null);
            if(r== JFileChooser.APPROVE_OPTION)
            {
                File file=new File(fileChooser.getSelectedFile().getAbsolutePath());
                try
                {
                    FileWriter wr=new FileWriter(file,false);
                    BufferedWriter w=new BufferedWriter(wr);
                    w.write(textArea.getText());
                    w.flush();
                    w.close();
                } catch (Exception evt) 
                {
                    JOptionPane.showMessageDialog(frame, evt.getMessage());
                }
            }
            else
            {
                JOptionPane.showMessageDialog(frame, "Operation cancelled !");
            }
        }
    }
    
}
public class javaEditor
{
    public static void main(String[] args) 
    {
        editor e=new editor();
    }
    
}
class JavaSourceFromString extends SimpleJavaFileObject {
    final String code;
  
    JavaSourceFromString(String name, String code) {
      super(URI.create("string:///" + name.replace('.','/') + Kind.SOURCE.extension),Kind.SOURCE);
      this.code = code;
    }
    
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) {
      return code;
    }
  }
