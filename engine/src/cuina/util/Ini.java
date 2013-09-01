package cuina.util;
 
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
 
/**
 * Ermöglicht persistentes Speichern und Auslesen von Informationen in Ini-Dateien.
 * Die Informationen werden als Schlüssel-Wert-Paaren vorgehalten, welche zusätzlich gruppiert werden können.
 * <p>
 * <i>Im Gegensatz zur <code>Properties</code>-Klasse können die Einträge in Sektionen unterteilt werden
 * und vorhandene Formatierungen werden auch beim Schreiben weitgehen beibehalten.
 * Somit wird die Datei lesbarer.</i>
 * In jeder Sektion darf ein Schlüssel nur einmal vorkommen.
 * </p>
 * <p>
 * Es können auf Referenzen auf andere Schlüssel oder Systemproperties gelegt werden.
 * Dies ist mit <code>${REFERENZ}</code> möglich.
 * Dabei ist <code>REFERENZ</code> entweder der Vollqualifizierte Name der Eigenschafft. Zum Beispiel:
 * <pre>foo.bar</pre> für die Eigenschaft <i>bar</i> in der Sektion <i>foo</i> oder der Name der Systemproperty:
 * <pre>System.getProperty("foobar");</pre>
 * Die interne Eigenschaft hat dabei Vorrang und die Auflösung der Systemproperties kann optional deaktivieert werden.
 * </p>
 * @author TheWhiteShadow
 * @version 2.1
 */
public class Ini
{
	/** Gibt an, ob beim Auflösen von Referenzen Systemproperties aufgelöst werden sollen. */
	public static boolean resolveSystemProperties = true;
	
    private File file;
    private Section section = new Section(0, "default"); // erstelle eine leere Gruppe
    private HashMap<String, Entry> data = new HashMap<String, Entry>();
    private ArrayList<Section> sections = new ArrayList<Section>();
    private static String lineSeparator = System.getProperty("line.separator");
    private int line;
    private boolean change = false;
    private boolean continueParsing = true;
   
    
    public Ini(File file) throws IOException, InvalidFileFormatException
    {
        this.file = file;
        if (file.exists())
        {
        	sections.clear();
        	sections.add(section);
            read();
        }
        else
        {
            file.createNewFile();
            sections.add(section);
            change = true;
        }
    }
    
    public Ini(String fileName) throws IOException, InvalidFileFormatException
    {
        this(new File(fileName));
    }
    
    public File getFile()
    {
        return file;
    }
    
    private void read() throws IOException, InvalidFileFormatException
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        
        String text;
        for(line = 1; (text = reader.readLine()) != null; line++)
        {
            readLine(text);
        }
        reader.close();
    }
    
    private void readLine(String text) throws InvalidFileFormatException
    {
        String name = null;
        String value = null;
        int end;
        char c;
        
        for(int i = 0; i < text.length(); i++)
        {
            c = text.charAt(i);
            if (c <= ' ') continue;
            switch(c)
            {
//              case ' ': break;
//              case '"':
//                  end = text.indexOf(i + 1, '"');
//                  if (end != -1) i = end;
//                  continue;
                case ';':
                case '#':
                    section.addEntry(new Entry(line, Entry.COMMENT, null, text));
                    return;
                case '=':
                    if(i > 0)
                    {
                        name = text.substring(0, i).trim();
                        if (section.get(name) != null)
                        {
                            throwException("Dublicate Key: " + name);
                            return;
                        }
                        value = readValue(text, i + 1);
                        section.addEntry(new Entry(line, Entry.PROPERTY, name, value));
                        return;
                    }
                    break;
                case '[':
                    if(i == 0 && text.trim().endsWith("]"))
                    {
                        end = text.indexOf(']');
                        if (end > 1)
                        {
                            String groupName = text.substring(1, end);
                            if (getSection(groupName) != null)
                            {
                                throwException("Dublicate Section: " + groupName);
                                return;
                            }
                            section = new Section(line, groupName);
                            sections.add(section);
                            return;
                        }
                    }
                    throwException("invalid section: " + text);
                    return;
            }
        }
        if (text.trim().length() > 0)
            throwException("invalid syntax: " + text);
    }
    
    private void throwException(String message) throws InvalidFileFormatException
    {
        InvalidFileFormatException e = new InvalidFileFormatException(line, message);
        if (!continueParsing)
            throw e;
        else
            System.err.println(e);
    }
    
    private String readValue(String segment, int start) throws InvalidFileFormatException
    {
        char c;
        int first = -1;
        for(int pos = start; pos < segment.length(); pos++)
        {
            c = segment.charAt(pos);
            switch(c)
            {
                case ' ':
                    continue;
//              case '\\':
//                  pos++;
//                  continue;
                case '$':
                    if (segment.charAt(pos+1) == '{')
                    {
                         if(segment.indexOf("}", pos) == -1)
                         {
                             throwException("invalid reference: " + segment);
                             return null;
                         }
                    }
                    break;
                case '"':
                    if (first == -1)
                        first = -(pos+1);
                    else
                        return segment.substring(-first, pos);
                    break;
            }
            if (first == -1) first = pos;
        }
        if (first == -1)
            return null;
        else
            return segment.substring(first).trim();
    }
    
    
    /**
     * Schreibt die Informationen zurück in die Datei.
     * @throws IOException
     */
    public void write() throws IOException
    {
         write(file);
    }
    
    /**
     * Schreibt die Informationen in die angegebene Datei.
     * @param file
     * @throws IOException
     */
    public void write(File file) throws IOException
    {
        if (!change) return;
        StringBuilder builder = new StringBuilder();
        
        Section s;
        Entry e;
        int line = 1;
        for(int i = 0; i < sections.size(); i++)
        {
            s = sections.get(i);
            if (s != null)
            {
                while (line < s.line)
                {
                	builder.append(lineSeparator);
                    line++;
                }
                
                if (i > 0)
                {
                	builder.append(lineSeparator + s + lineSeparator);
                	line += 2;
                }
                
                for(int j = 0; j < s.entries.size(); j++)
                {
                    e = s.entries.get(j);
                    if (e != null)
                    {
                        while (line < e.line)
                        {
                        	builder.append(lineSeparator);
                            line++;
                        }
                        builder.append(e + lineSeparator);
                        line++;
                    }
                }
            }
        }
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
        writer.write(builder.toString());
        writer.close();
    }
    
    public Section getSection(String name)
    {
        if (name == null || name == "") return sections.get(0);
        for(Section g : sections)
        {
            if (name.equals(g.name)) return g;
        }
        return null;
    }
    
    public Section[] getSections()
    {
        return sections.toArray(new Section[sections.size()]);
    }
 
    public void set(Section section, String name, String value)
    {
        section.set(name, value);
    }
    
    public void set(String section, String name, String value)
    {
        Section g = getSection(section);
        if (g == null)
        {
            int line = sections.get(sections.size() - 1).line + 2;
            g = new Section(line, section);
            sections.add(g);
        }
        g.set(name, value);
    }
 
    public String get(Section section, String name)
    {
        return get(section, name, null);
    }
    
    public String get(String section, String name)
    {
        return get(section, name, null);
    }
 
    public String get(Section section, String name, String defaultValue)
    {
        return section.get(name, defaultValue);
    }
    
    public String get(String section, String name, String defaultValue)
    {
        if (section == "") section = null;
        
        Section g = getSection(section);
        if (g == null) return defaultValue;
        return g.get(name, defaultValue);
    }
    
    public void updateLines(Section ref, int count)
    {
        boolean move = false;
        for(Section s : sections)
        {
            if (move)
            {
                s.line += count;
                for(Entry e : s.entries)
                {
                    e.line += count;
                }
            }
            if (ref == s) move = true;
        }
    }
    
    public class Section
    {
        ArrayList<Entry> entries = new ArrayList<Entry>();
        int line;
        String name;
        
        private Section(int line, String name)
        {
            this.line = line;
            this.name = name;
        }
        
        public void set(String name, String value)
        {
            Entry e = getEntry(name);
            if (e == null)
            {
                int newLine;
                if (entries.isEmpty())
                    newLine = this.line + 1;
                else
                    newLine = entries.get(entries.size() - 1).line + 1;
                addEntry(new Entry(newLine, Entry.PROPERTY, name, value));
                updateLines(this, 1);
            }
            else
            {
                if (e.value.equals(value)) return;
                e.value = value;
            }
            change = true;
        }
        
        private void addEntry(Entry e)
        {
            entries.add(e);
            if (e.type == Entry.PROPERTY)
                data.put(name + "." + e.name, e);
        }
        
        public Entry getEntry(String key)
        {
            if (key == null) return null;
            return data.get(name + "." + key);
        }
        
        public Entry[] getEntries()
        {
            ArrayList<Entry> list = new ArrayList<Entry>(entries.size());
            for(Entry e : entries)
            {
                if (e.type == Entry.PROPERTY) list.add(e);
            }
            return list.toArray(new Entry[list.size()]);
        }
 
        public String get(String key)
        {
            return get(key, null);
        }
        
        public String get(String key, String defaultValue)
        {
            Entry e = getEntry(key);
            if (e != null)
                return e.getValue();
            else
                return defaultValue;
        }
        
        public String getName()
        {
        	return name;
        }
        
        @Override
        public String toString()
        {
            return "[" + name + "]";
        }
    }
    
    public class Entry
    {
        private static final int COMMENT = 1;
        private static final int PROPERTY = 2;
        
        private int line;
        private int type;
        private String name;
        private String value;
        
        private boolean resolving = false;
        
        private Entry(int line, int type, String name, String value)
        {
            this.line = line;
            this.type = type;
            this.name = name;
            this.value = value;
        }
 
        public String getValue()
        {
            if (resolving) 
                throw new IllegalStateException("(line " + line + "): Try to resolve circle-reference");
            if (value != null && value.startsWith("${")) try
        	{	// Wenn e == this ist, wird eine IllegalStateException geworfen.
                resolving = true;
                String refName = value.substring(2, value.indexOf('}'));
                String result = null;
                Entry e = data.get(refName);
                if (e == null)
                {
                	if (resolveSystemProperties)
                		result = System.getProperty(refName);
                }
                else
                {
                	result = e.getValue();
                }
                return result;
        	}
        	finally
        	{	// stelle Sicher, dass das Flag zurückgesetzt wird.
                resolving = false;
        	}
            return value;
        }
 
        public void setValue(String value)
        {
            this.value = value;
        }
 
        public String getName()
        {
            return name;
        }
 
        @Override
        public String toString()
        {
            switch(type)
            {
                case COMMENT: return value;
                case PROPERTY: 
                    if (value != null)
                        return name + "=\"" + value + "\"";
                    else
                        return name + "=";
            }
            return "";
        }
    }
    
    public static void main(String[] args)
    {
        try
        {
            Ini ini = new Ini("cuina.ini");
            ini.set("Game", "Title", "Name");
            
//          ini.set("Section2", "neuer_name", "neuer Wert");
            Section[] list = ini.getSections();
            
            for(Section s : list)
            {
                System.out.println(s);
                Entry[] entries = s.getEntries();
                for(Entry e : entries)
                {
                    System.out.println(e.name + " = " + e.getValue());
                }
            }
 
            ini.write(new File("cuina2.ini"));
        }
        catch (IOException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (InvalidFileFormatException e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
}