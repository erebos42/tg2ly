package tg2ly;

public class Main {

	public static String versionString = "0.1.0";
	
	private class CmdLineParameters
	{
		public String in_path = null;
		public String out_path = null;
		public boolean help_marker = false;
		public boolean version_marker = false;
	}
	
	private Main.CmdLineParameters parseCmd(String[] args)
	{
		Main.CmdLineParameters params = new Main.CmdLineParameters();
		boolean in_marker   = false;
		boolean out_marker  = false;
		
        for (String s: args) {
        	if (s.equals("--in"))
        	{
        		in_marker = true;
        		continue;
        	}
        	if (s.equals("--out"))
        	{
        		out_marker = true;
        		continue;
        	}
        	if (s.equals("--help") || s.equals("-h"))
        	{
        		params.help_marker = true;
        	}
        	if (s.equals("--version") || s.equals("-v"))
        	{
        		params.version_marker = true;
        	}
        	
        	if (in_marker)
        	{
        		in_marker = false;
        		params.in_path = s;
        	}
        	if (out_marker)
        	{
        		out_marker = false;
        		params.out_path = s;
        	}
        }
        
        if (params.in_path == null)
        {
        	params.help_marker = true;
        }
		
		return params;
	}
	
	private void display_help()
	{
		System.out.println("tg2ly [--version|-v] [--help|-h] --in [in_path] --out [out_path]");
	}
	
	private void display_version()
	{
		System.out.println("Tg2Ly - Version " + versionString);
		System.out.println("Licensed under LGPL");
	}
	
	public static void main(String[] args) {
		Main m = new Main();
		CmdLineParameters params = m.parseCmd(args);
	
        if (params.version_marker)
        {
        	m.display_version();
        }
        else if (params.help_marker)
        {
        	m.display_help();
        }
        else
        {
    		LyBookExport bookExporter = new LyBookExport();
    		try {
    			bookExporter.exportBook(params.in_path, params.out_path);
    		}
    		catch (Exception e)
    		{
    			//System.out.println("Invalid input arguments!");
    			e.printStackTrace();
    		}        	
        }
	}
}
