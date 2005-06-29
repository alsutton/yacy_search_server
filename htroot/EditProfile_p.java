//EditProfile_p.java 
//-----------------------
//part of YACY
//(C) by Michael Peter Christen; mc@anomic.de
//first published on http://www.anomic.de
//Frankfurt, Germany, 2004, 2005
//
//This File is contributed by Alexander Schier
//last change: 29.06.2005 by Marc Nause
//
//This program is free software; you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation; either version 2 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program; if not, write to the Free Software
//Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//
//Using this software in any meaning (reading, learning, copying, compiling,
//running) means that you agree that the Author(s) is (are) not responsible
//for cost, loss of data or any harm that may be caused directly or indirectly
//by usage of this softare or this documentation. The usage of this software
//is on your own risk. The installation and usage (starting/running) of this
//software may allow other people or application to access your computer and
//any attached devices and is highly dependent on the configuration of the
//software which must be done by the user of the software; the author(s) is
//(are) also not responsible for proper configuration and usage of the
//software, even if provoked by documentation provided together with
//the software.
//
//Any changes to this file according to the GPL as documented in the file
//gpl.txt aside this file in the shipment you received can be done to the
//lines that follows this copyright notice here, but changes must not be
//done inside the copyright notive above. A re-distribution must contain
//the intact and unchanged copyright notice.
//Contributions and changes to the program code must be marked as such.

//You must compile this file with
//javac -classpath .:../Classes Blacklist_p.java
//if the shell's current path is HTROOT

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import de.anomic.http.httpHeader;
import de.anomic.server.serverObjects;
import de.anomic.server.serverSwitch;
import de.anomic.tools.unHtml;

public class EditProfile_p {
    
    public static serverObjects respond(httpHeader header, serverObjects post, serverSwitch env) {
        //listManager.switchboard = (plasmaSwitchboard) env;
        serverObjects prop = new serverObjects();
        Properties profile = new Properties();
        FileInputStream fileIn = null;
        try{
            fileIn = new FileInputStream(new File("DATA/SETTINGS/profile.txt"));
            profile.load(fileIn);            
        } catch(IOException e){            
        } finally {
            if (fileIn != null) try { fileIn.close(); } catch (Exception e) {}
        }
       
        if(post != null && post.containsKey("set")){
            profile.setProperty("name", (String)post.get("name"));
            profile.setProperty("nickname", (String)post.get("nickname"));
            profile.setProperty("homepage", (String)post.get("homepage"));
            profile.setProperty("email", (String)post.get("email"));
            
            profile.setProperty("icq", (String)post.get("icq"));
            profile.setProperty("jabber", (String)post.get("jabber"));
            profile.setProperty("yahoo", (String)post.get("yahoo"));
            profile.setProperty("msn", (String)post.get("msn"));
            
            profile.setProperty("comment", (String)post.get("comment"));
        }
        prop.put("name", (new unHtml(profile.getProperty("name", ""))).un());
        prop.put("nickname", (new unHtml(profile.getProperty("nickname", ""))).un());
        prop.put("homepage", (new unHtml(profile.getProperty("homepage", ""))).un());
        prop.put("email", (new unHtml(profile.getProperty("email", ""))).un());
        
        prop.put("icq", (new unHtml(profile.getProperty("icq", ""))).un());
        prop.put("jabber", (new unHtml(profile.getProperty("jabber", ""))).un());
        prop.put("yahoo", (new unHtml(profile.getProperty("yahoo", ""))).un());
        prop.put("msn", (new unHtml(profile.getProperty("msn", ""))).un());
        
        prop.put("comment", (new unHtml(profile.getProperty("comment", ""))).un());
         
        FileOutputStream fileOut = null;
        try{
            fileOut = new FileOutputStream(new File("DATA/SETTINGS/profile.txt"));
            profile.store(fileOut , null );
        }catch(IOException e){             
        } finally {
            if (fileOut != null) try { fileOut.close(); } catch (Exception e) {}
        }
        
        return prop;
    }
    
}
