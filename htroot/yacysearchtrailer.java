// yacysearchitem.java
// (C) 2007 by Michael Peter Christen; mc@yacy.net, Frankfurt a. M., Germany
// first published 28.08.2007 on http://yacy.net
//
// This is a part of YaCy, a peer-to-peer based web search engine
//
// $LastChangedDate: 2006-04-02 22:40:07 +0200 (So, 02 Apr 2006) $
// $LastChangedRevision: 1986 $
// $LastChangedBy: orbiter $
//
// LICENSE
// 
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

import java.util.ArrayList;
import java.util.Iterator;

import de.anomic.http.httpRequestHeader;
import de.anomic.plasma.plasmaProfiling;
import de.anomic.plasma.plasmaSearchEvent;
import de.anomic.plasma.plasmaSearchQuery;
import de.anomic.plasma.plasmaSearchRankingProcess.NavigatorEntry;
import de.anomic.server.serverObjects;
import de.anomic.server.serverProfiling;
import de.anomic.server.serverSwitch;


public class yacysearchtrailer {

    private static final int MAX_TOPWORDS = 24;
    
    public static serverObjects respond(final httpRequestHeader header, final serverObjects post, final serverSwitch<?> env) {
        final serverObjects prop = new serverObjects();
        
        final String eventID = post.get("eventID", "");
        final int display = post.getInt("display", 0);
        
        // find search event
        final plasmaSearchEvent theSearch = plasmaSearchEvent.getEvent(eventID);
        if (theSearch == null) {
            // the event does not exist, show empty page
            return prop;
        }
        final plasmaSearchQuery theQuery = theSearch.getQuery();
        
        // compose search navigation
        
        // host navigators
        ArrayList<NavigatorEntry> hostNavigator = theSearch.getHostNavigator(10);
        if (hostNavigator == null) {
        	prop.put("nav-domains", 0);
        } else {
        	prop.put("nav-domains", 1);
        	NavigatorEntry entry;
        	int i;
        	for (i = 0; i < hostNavigator.size(); i++) {
        		entry = hostNavigator.get(i);
        		prop.put("nav-domains_element_" + i + "_name", entry.name);
                prop.put("nav-domains_element_" + i + "_url", "<a href=\"" + plasmaSearchQuery.navurl("html", 0, display, theQuery, theQuery.urlMask, "site:" + entry.name, theQuery.navigators) + "\">" + entry.name + " (" + entry.count + ")</a>");
        		prop.putJSON("nav-domains_element_" + i + "_url-json", plasmaSearchQuery.navurl("json", 0, display, theQuery, theQuery.urlMask, "site:" + entry.name, theQuery.navigators));
                prop.put("nav-domains_element_" + i + "_count", entry.count);
        		prop.put("nav-domains_element_" + i + "_modifier", "site:" + entry.name);
                prop.put("nav-domains_element_" + i + "_nl", 1);
        	}
        	i--;
        	prop.put("nav-domains_element_" + i + "_nl", 0);
        	prop.put("nav-domains_element", hostNavigator.size());
        }
        
        // attach the bottom line with search references (topwords)
        final ArrayList<NavigatorEntry> references = theSearch.getTopicNavigator(10);
        // default settings for no topics
        prop.put("nav-topics", "0");
        
        if (references.size() > 0) {
            int i = 0;
            NavigatorEntry e;
            Iterator<NavigatorEntry> iter = references.iterator();
            while (iter.hasNext()) {
            	e = iter.next();
                if (/*(theQuery == null) ||*/ (theQuery.queryString == null)) break;
                if (e != null && e.name != null) {
                    prop.putHTML("nav-topics_element_" + i + "_name", e.name);
                    prop.put("nav-topics_element_" + i + "_url", "<a href=\"" + plasmaSearchQuery.navurl("html", 0, display, theQuery, theQuery.urlMask, e.name, theQuery.navigators) + "\">" + e.name + " (" + e.count + ")</a>");
                    prop.putJSON("nav-topics_element_" + i + "_url-json", plasmaSearchQuery.navurl("json", 0, display, theQuery, theQuery.urlMask, e.name, theQuery.navigators));
                    prop.put("nav-topics_element_" + i + "_count", e.count);
                    prop.put("nav-topics_element_" + i + "_modifier", e.name);
                    prop.put("nav-topics_element_" + i + "_nl", (iter.hasNext() && i < MAX_TOPWORDS) ? 1 : 0);
                }
                if (i++ > MAX_TOPWORDS) {
                    break;
                }
            }
            prop.put("nav-topics_element", i);
            prop.put("nav-topics", "1");
        }
        
        // author navigators
        ArrayList<NavigatorEntry> authorNavigator = theSearch.getAuthorNavigator(10);
        if (authorNavigator == null) {
            prop.put("nav-authors", 0);
        } else {
            prop.put("nav-authors", 1);
            NavigatorEntry entry;
            int i;
            for (i = 0; i < authorNavigator.size(); i++) {
                entry = authorNavigator.get(i);
                prop.put("nav-authors_element_" + i + "_name", entry.name);
                prop.put("nav-authors_element_" + i + "_url", "<a href=\"" + plasmaSearchQuery.navurl("html", 0, display, theQuery, theQuery.urlMask, "author:'" + entry.name + "'", theQuery.navigators) + "\">" + entry.name + " (" + entry.count + ")</a>");
                prop.putJSON("nav-authors_element_" + i + "_url-json", plasmaSearchQuery.navurl("json", 0, display, theQuery, theQuery.urlMask, "author:'" + entry.name + "'", theQuery.navigators));
                prop.put("nav-authors_element_" + i + "_count", entry.count);
                prop.put("nav-authors_element_" + i + "_modifier", "author:'" + entry.name + "'");
                prop.put("nav-authors_element_" + i + "_nl", 1);
            }
            i--;
            prop.put("nav-authors_element_" + i + "_nl", 0);
            prop.put("nav-authors_element", authorNavigator.size());
        }

        serverProfiling.update("SEARCH", new plasmaProfiling.searchEvent(theQuery.id(true), plasmaSearchEvent.FINALIZATION + "-" + "bottomline", 0, 0), false);
        
        return prop;
    }

}
