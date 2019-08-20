/*
************************************************************************
*******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
**************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
*
*  (c) 2009.                            (c) 2009.
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
*  $Revision: 4 $
*
************************************************************************
*/

package ca.nrc.cadc.wcs;

import ca.nrc.cadc.util.Log4jInit;
import ca.nrc.cadc.wcs.Transform.Result;
import ca.nrc.cadc.wcs.exceptions.WCSLibRuntimeException;
import org.apache.logging.log4j.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Port to Java of the twcs.c C method. twcs.c is used to verify the compilation
 * and installation of the WCSLIB 4.2 C library.
 * 
 *  This Java port is used to verify the compilation and installation of the
 *  Java JNI implementation of the WCSLIB methods wcss2p() and wcsp2s().
 * 
 * @author jburke
 *
 */
public class WCSLibTest
{
    private static final Logger log = LogManager.getLogger(WCSLibTest.class);
    
    public WCSLibTest()
    {
    }
    
    @Test
    public void testTransform()
    {
        
        try
        {
            WCSKeywords keywords = getTestKeywords();
            Transform transform = new Transform(keywords);
            doWCSTest(transform, 4);
        }
        catch (Exception unexpected)
        {
            log.error("unexpected exception", unexpected);
            Assert.fail("unexpected exception: " + unexpected);
        }
    }

    @Test
    public void testTranslate()
    {
        try
        {
            WCSKeywords keywords = getTranslateKeywords();

            keywords.put("LONPOLE", 180.0D);
            keywords.put("LATPOLE", 180.0D);
            keywords.put("RESTWAV", 180.0D);

            Transform transform = new Transform(keywords);
            log.debug("keywords to be translated:\n " + transform.toString());
            WCSKeywords keywords2 = transform.translate("WAVE-???");
            Transform returned = new Transform(keywords2);
            log.debug("translated keywords:\n " + returned.toString());
        }
        catch (Exception unexpected)
        {
            log.error("unexpected exception", unexpected);
            Assert.fail("unexpected exception: " + unexpected);
        }
    }
    
    @Test
    public void testVertices()
    {
        try
        {
            WCSKeywords keywords = getVerticesKeywords();
            Transform transform = new Transform(keywords);
            doVertices(transform, 4);
        }
        catch (Exception unexpected)
        {
            log.error("unexpected exception", unexpected);
            Assert.fail("unexpected exception: " + unexpected);
        }
    }

    @Test
    public void testPix2Sky()
    {
        try
        {
            WCSKeywords keywords = getPix2SkyKeywords();
            Transform transform = new Transform(keywords);
            Result result1 = transform.pix2sky( new double[] { 0.5, 1.5 });
            log.debug("1st transform: " + result1.coordinates[0] + "(" + result1.units[0] + "), ");
            log.debug(result1.coordinates[1] + "(" + result1.units[1] + ")");

            Result result2 = transform.pix2sky( new double[] { 185600 + 0.5, 185600 + 1.5 });
            log.debug("2nd transform: " + result2.coordinates[0] + "(" + result2.units[0] + "), ");
            log.debug(result2.coordinates[1] + "(" + result2.units[1] + ")\n");
        }
        catch (Exception unexpected)
        {
            log.error("unexpected exception", unexpected);
            Assert.fail("unexpected exception: " + unexpected);
        }
    }
    
    @Test
    public void testErrors()
    {
        try
        {
            WCSKeywords keywords = getSingularMatrixKeywords();
            Transform transform = new Transform(keywords);
            Result result = transform.pix2sky( new double[] { 0.5, 0.5 });
            Assert.fail("Expected WCSLibRuntimeException");
        }
        catch (WCSLibRuntimeException e)
        {
            Assert.assertEquals("pix2sky test", "Linear transformation matrix is singular(3)", e.getMessage());
        }
        catch (Exception unexpected)
        {
            log.error("unexpected exception", unexpected);
            Assert.fail("unexpected exception: " + unexpected);
        }
            
        try
        {
            WCSKeywords keywords = getSingularMatrixKeywords();
            Transform transform = new Transform(keywords);
            Result result = transform.sky2pix( new double[] { 0.5, 0.5 });
            Assert.fail("Expected WCSLibRuntimeException");
        }
        catch (WCSLibRuntimeException e)
        {
            Assert.assertEquals("sky2pix test", "Linear transformation matrix is singular(3)", e.getMessage());
        }
        catch (Exception unexpected)
        {
            log.error("unexpected exception", unexpected);
            Assert.fail("unexpected exception: " + unexpected);
        }
            
        try
        {
            WCSKeywords keywords = getSingularMatrixKeywords();
            Transform transform = new Transform(keywords);
            WCSKeywords result = transform.translate("WAVE-???");
            Assert.fail("Expected WCSLibRuntimeException");
        }
        catch (WCSLibRuntimeException e)
        {
            Assert.assertEquals("translate test", "Linear transformation matrix is singular(3)", e.getMessage());
        }
        catch (Exception unexpected)
        {
            log.error("unexpected exception", unexpected);
            Assert.fail("unexpected exception: " + unexpected);
        }
    }

    private static void doWCSTest(Transform transform, int NAXIS)
    {
        int NCOORD = 361;
        int NELEM = 9;
        double[][] world1 = new double[NCOORD][NELEM];
        double time = 1.0;
        double freq = 1.42040595e9 - 180.0 * 62500.0;
        for (int k = 0; k < NCOORD; k++)
        {
           world1[k][0] = 0.0;
           world1[k][1] = 0.0;
           world1[k][2] = 0.0;
           world1[k][3] = 0.0;

           world1[k][2] = time;
           time *= 1.01;

           world1[k][0] = 2.99792458e8 / freq;
           freq += 62500.0;
        }

        int k;
        int lng;
        double lat1;
        double lng1;
        for (int lat = 90; lat >= -90; lat--)
        {
            lat1 = (double) lat;

            for (lng = -180, k = 0; lng <= 180; lng++, k++)
            {
               lng1 = (double) lng;

               world1[k][3] = lng1;
               world1[k][1] = lat1;
            }
             
            // sky -> pix -> sky and then compare
            Result pixel1 = null;
            try
            {
               pixel1 = transform.sky2pix(world1[0]);
               Assert.assertNotNull(pixel1);
               Assert.assertNotNull(pixel1.coordinates);
            }
            catch (Exception unexpected)
            {
                log.error("unexpected exception", unexpected);
                Assert.fail("unexpected exception: " + unexpected);
            }
           
            Result sky1 = null;
            try
            {
               sky1 = transform.pix2sky(pixel1.coordinates);
               Assert.assertNotNull(sky1);
               Assert.assertNotNull(sky1.coordinates);
               
               // world1 == sky1
            }
            catch (Exception unexpected)
            {
                log.error("unexpected exception", unexpected);
                Assert.fail("unexpected exception: " + unexpected);
            }

            // what is the point of this?
            Result pixel2 = null;
            try
            {
                pixel2 = transform.sky2pix(sky1.coordinates);
                Assert.assertNotNull(pixel2);
                Assert.assertNotNull(pixel2.coordinates);
                
                // pixel1 == pixel2?
            }
            catch (Exception unexpected)
            {
                log.error("unexpected exception", unexpected);
                Assert.fail("unexpected exception: " + unexpected);
            }
        }
    }

    private static void doVertices(Transform transform, int NAXIS)
    {
        double[] pixel = new double[2];
        
        try
        {
            pixel[0] = 0.5;
            pixel[1] = 0.5;
            Result result = transform.pix2sky(pixel);
            Assert.assertNotNull(result);
            Assert.assertNotNull(result.coordinates);
            Assert.assertNotNull(result.units);
            log.debug(pixel [0] +", " +  pixel[1] + " -> ");
            log.debug( + result.coordinates[0] + ", " + result.coordinates[1]);
            log.debug(" units(" + result.units[0] + ", " + result.units[1] + ")");

            pixel[0] = 0.5;
            pixel[1] = 2248.5;
            result = transform.pix2sky(pixel);
            Assert.assertNotNull(result);
            Assert.assertNotNull(result.coordinates);
            Assert.assertNotNull(result.units);
            log.debug(pixel [0] +", " +  pixel[1] + " -> ");
            log.debug( + result.coordinates[0] + ", " + result.coordinates[1]);
            log.debug(" units(" + result.units[0] + ", " + result.units[1] + ")");

            pixel[0] = 8.5;
            pixel[1] = 2248.5;
            result = transform.pix2sky(pixel);
            Assert.assertNotNull(result);
            Assert.assertNotNull(result.coordinates);
            Assert.assertNotNull(result.units);
            log.debug(pixel [0] +", " +  pixel[1] + " -> ");
            log.debug( + result.coordinates[0] + ", " + result.coordinates[1]);
            log.debug(" units(" + result.units[0] + ", " + result.units[1] + ")");

            pixel[0] = 8.5;
            pixel[1] = 0.5;
            result = transform.pix2sky(pixel);
            Assert.assertNotNull(result);
            Assert.assertNotNull(result.coordinates);
            Assert.assertNotNull(result.units);
            log.debug(pixel [0] +", " +  pixel[1] + " -> ");
            log.debug( + result.coordinates[0] + ", " + result.coordinates[1]);
            log.debug(" units(" + result.units[0] + ", " + result.units[1] + ")");

        }
        catch (Exception unexpected)
        {
            log.error("unexpected exception", unexpected);
            Assert.fail("unexpected exception: " + unexpected);
        }
    }
    
    
    

    private static WCSKeywords getTranslateKeywords()
    {
        WCSKeywords keywords = new WCSKeywordsImpl();

        keywords.put("CDELT1", 97656.25D);
        keywords.put("CRPIX1", 32.0D);
        keywords.put("CRVAL1", 1.378351174e9D);
        keywords.put("CTYPE1", "FREQ");
        keywords.put("CUNIT1", "Hz");
        
        keywords.put("RESTFRQ", 1.420405752e9D);
        keywords.put("MJD-AVG", 51085.98247222222D);
        keywords.put("MJD-OBS", 51085.979D);
        keywords.put("NAXIS", 1);
        keywords.put("NAXIS1", 63);
        keywords.put("SPECSYS", "TOPOCENT");
        keywords.put("VELOSYS", 26108.0D);

         return keywords;
    }
    
    private static WCSKeywords getTestKeywords()
    {
        WCSKeywords keywords = new WCSKeywordsImpl();
        
        keywords.put("NAXIS", 4);
        
        keywords.put("CRPIX1", 513.0D);
        keywords.put("CRPIX2", 0.0D);
        keywords.put("CRPIX3", 0.0D);
        keywords.put("CRPIX4", 0.0D);
        
        keywords.put("PC1_1", 1.1D);
        keywords.put("PC1_2", 0.0D);
        keywords.put("PC1_3", 0.0D);
        keywords.put("PC1_4", 0.0D);
        keywords.put("PC2_1", 0.0D);
        keywords.put("PC2_2", 1.0D);
        keywords.put("PC2_3", 0.0D);
        keywords.put("PC2_4", 0.1D);
        keywords.put("PC3_1", 0.0D);
        keywords.put("PC3_2", 0.0D);
        keywords.put("PC3_3", 1.0D);
        keywords.put("PC3_4", 0.0D);
        keywords.put("PC4_1", 0.0D);
        keywords.put("PC4_2", 0.2D);
        keywords.put("PC4_3", 0.0D);
        keywords.put("PC4_4", 1.0D);
        
        keywords.put("CDELT1", -9.635265432e-6D);
        keywords.put("CDELT2", 1.0D);
        keywords.put("CDELT3", 0.1D);
        keywords.put("CDELT4", -1.0D);
        
        keywords.put("CRVAL1", 0.214982042D);
        keywords.put("CRVAL2", -30.0D);
        keywords.put("CRVAL3", 1.0D);
        keywords.put("CRVAL4", 150.0D);
        
        keywords.put("CTYPE1", "WAVE-F2W");
        keywords.put("CTYPE2", "XLAT-BON");
        keywords.put("CTYPE3", "TIME-LOG");
        keywords.put("CTYPE4", "XLON-BON");
        
        keywords.put("LONPOLE", 150.0D);
        keywords.put("LATPOLE", 999.0D);
        keywords.put("RESTFRQ", 1.42040575e9D);
        keywords.put("RESTWAV", 0.0D);
        
        keywords.put("PV4_1", 0.0D);
        keywords.put("PV4_2", 90.0D);
        keywords.put("PV2_1", -30.0D); 

        return keywords;
    }

    private static WCSKeywords getVerticesKeywords()
    {
        WCSKeywords keywords = new WCSKeywordsImpl();
        
        keywords.put("CD1_1", -1.74368624704027E-4);
        keywords.put("CD1_2", 2.45291066786335E-7);
        keywords.put("CD2_1", 9.3961005590014E-7);
        keywords.put("CD2_2", 1.74110153624481E-4);

        keywords.put("CRPIX1", 183.97113410273);
        keywords.put("CRPIX2", 2217.53366109879);

        keywords.put("CRVAL1", 76.3387499968215);
        keywords.put("CRVAL2", -69.08717903879);

        keywords.put("CTYPE1", "RA---TNX");
        keywords.put("CTYPE2", "DEC--TNX");

        keywords.put("CUNIT1", "deg");
        keywords.put("CUNIT2", "deg");

        keywords.put("EQUINOX", 2000.0);
        keywords.put("NAXIS", 2);
        keywords.put("NAXIS1", 8);
        keywords.put("NAXIS2", 2248);
        keywords.put("RADECSYS", "FK5");

        return keywords;
    }

    private static WCSKeywords getPix2SkyKeywords()
    {
        WCSKeywords keywords = new WCSKeywordsImpl();

        keywords.put("CDELT1", 0.00339439);
        keywords.put("CRPIX1", 0.5);
        keywords.put("CRVAL1", 370.0);
        keywords.put("CTYPE1", "WAVE");
        keywords.put("CUNIT1", "nm");
        keywords.put("NAXIS", 2);
        keywords.put("NAXIS1", 185600);

        keywords.put("CDELT2", 0.00339439);
        keywords.put("CRPIX2", 0.5);
        keywords.put("CRVAL2", 370.0);
        keywords.put("CTYPE2", "WAVE");
        keywords.put("CUNIT2", "m");
        keywords.put("NAXIS2", 185600);

         return keywords;
    }
    
    private static WCSKeywords getSingularMatrixKeywords()
    {
        WCSKeywords keywords = new WCSKeywordsImpl();
        
        keywords.put("CD1_1", 1);
        keywords.put("CD1_2", 0);
        keywords.put("CD2_1", 0);
        keywords.put("CD2_2", 0);

        keywords.put("CRPIX1", 183.97113410273);
        keywords.put("CRPIX2", 2217.53366109879);

        keywords.put("CRVAL1", 76.3387499968215);
        keywords.put("CRVAL2", -69.08717903879);

        keywords.put("CTYPE1", "RA---TNX");
        keywords.put("CTYPE2", "DEC--TNX");

        keywords.put("CUNIT1", "deg");
        keywords.put("CUNIT2", "deg");

        keywords.put("EQUINOX", 2000.0);
        keywords.put("NAXIS", 2);
        keywords.put("NAXIS1", 8);
        keywords.put("NAXIS2", 2248);
        keywords.put("RADECSYS", "FK5");

        return keywords;
    }

}
