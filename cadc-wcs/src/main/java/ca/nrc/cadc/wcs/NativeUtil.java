/*
************************************************************************
*******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
**************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
*
*  (c) 2011.                            (c) 2011.
*  Government of Canada                 Gouvernement du Canada
*  National Research Council            Conseil national de recherches
*  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
*  All rights reserved                  Tous droits réservés
*
*  NRC disclaims any warranties,        Le CNRC dénie toute garantie
*  expressed, implied, or               énoncée, implicite ou légale,
*  statutory, of any kind with          de quelque nature que ce
*  respect to the software,             soit, concernant le logiciel,
*  including without limitation         y compris sans restriction
*  any warranty of merchantability      toute garantie de valeur
*  or fitness for a particular          marchande ou de pertinence
*  purpose. NRC shall not be            pour un usage particulier.
*  liable in any event for any          Le CNRC ne pourra en aucun cas
*  damages, whether direct or           être tenu responsable de tout
*  indirect, special or general,        dommage, direct ou indirect,
*  consequential or incidental,         particulier ou général,
*  arising from the use of the          accessoire ou fortuit, résultant
*  software.  Neither the name          de l'utilisation du logiciel. Ni
*  of the National Research             le nom du Conseil National de
*  Council of Canada nor the            Recherches du Canada ni les noms
*  names of its contributors may        de ses  participants ne peuvent
*  be used to endorse or promote        être utilisés pour approuver ou
*  products derived from this           promouvoir les produits dérivés
*  software without specific prior      de ce logiciel sans autorisation
*  written permission.                  préalable et particulière
*                                       par écrit.
*
*  This file is part of the             Ce fichier fait partie du projet
*  OpenCADC project.                    OpenCADC.
*
*  OpenCADC is free software:           OpenCADC est un logiciel libre ;
*  you can redistribute it and/or       vous pouvez le redistribuer ou le
*  modify it under the terms of         modifier suivant les termes de
*  the GNU Affero General Public        la “GNU Affero General Public
*  License as published by the          License” telle que publiée
*  Free Software Foundation,            par la Free Software Foundation
*  either version 3 of the              : soit la version 3 de cette
*  License, or (at your option)         licence, soit (à votre gré)
*  any later version.                   toute version ultérieure.
*
*  OpenCADC is distributed in the       OpenCADC est distribué
*  hope that it will be useful,         dans l’espoir qu’il vous
*  but WITHOUT ANY WARRANTY;            sera utile, mais SANS AUCUNE
*  without even the implied             GARANTIE : sans même la garantie
*  warranty of MERCHANTABILITY          implicite de COMMERCIALISABILITÉ
*  or FITNESS FOR A PARTICULAR          ni d’ADÉQUATION À UN OBJECTIF
*  PURPOSE.  See the GNU Affero         PARTICULIER. Consultez la Licence
*  General Public License for           Générale Publique GNU Affero
*  more details.                        pour plus de détails.
*
*  You should have received             Vous devriez avoir reçu une
*  a copy of the GNU Affero             copie de la Licence Générale
*  General Public License along         Publique GNU Affero avec
*  with OpenCADC.  If not, see          OpenCADC ; si ce n’est
*  <http://www.gnu.org/licenses/>.      pas le cas, consultez :
*                                       <http://www.gnu.org/licenses/>.
*
*  $Revision: 5 $
*
************************************************************************
*/

package ca.nrc.cadc.wcs;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import org.apache.logging.log4j.*;

/**
 *
 * @author pdowler
 */
public class NativeUtil 
{
    private static final Logger log = LogManager.getLogger(NativeUtil.class);

    private NativeUtil() { }
    
    static void loadJNI(ClassLoader cl, String name, UUID uuid)
        throws NativeInitializationException
    {
        String osName = System.getProperty("os.name");
        String soExt = ".so";              // default is Linux
        if (osName.contains("OS X"))       // but check for OSX
          soExt = ".dylib";
        File tmpdir = new File(System.getProperty("java.io.tmpdir"));
        File tmp = new File(tmpdir, name + "-" + uuid.toString() + soExt);
        
        try
        {
            String soname = name + soExt;
            URL url = cl.getResource(soname);
            
            if (url == null)
                throw new NativeInitializationException("not found via ClassLoader: " + soname);
            
            log.debug("found: " + url);
            if (tmp.exists())
                // already been here: impossible?
                throw new NativeInitializationException("found pre-existing " + tmp.getAbsolutePath());

            try
            {
                URLConnection uc = url.openConnection();
                uc.setUseCaches(false);
                InputStream istream = uc.getInputStream();
                //InputStream istream = url.openStream();
                FileOutputStream ostream = new FileOutputStream(tmp);
                byte[] buf = new byte[65536];
                int nb = istream.read(buf);
                while (nb != -1)
                {
                    ostream.write(buf, 0, nb);
                    nb = istream.read(buf);
                }
                ostream.close();
                log.debug("extracted: " + url.toExternalForm() + " -> " + tmp.getAbsolutePath());

                System.load(tmp.getAbsolutePath());
                log.debug("loaded: " + tmp.getAbsolutePath());
            }
            finally
            {
                if (tmp.exists())
                    tmp.deleteOnExit();
            }
        }
        catch (IOException ex)
        {
            log.error("failed to load shared lib", ex);
            throw new NativeInitializationException("failed to create temporary file: " + tmp.getAbsolutePath(), ex);
        }
        catch (Error e)
        {
            log.error("failed to load shared lib", e);
            throw new NativeInitializationException("failed to load shared lib: " + name, e);
        }
    }
}
