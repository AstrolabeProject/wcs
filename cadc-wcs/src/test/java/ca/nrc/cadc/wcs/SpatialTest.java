/*
 * (c) Patrick Dowler, 2012.
 *
 * This software is released under the GNU GPL version 3 or later.
 * See the LICENSE document included with the source or
 * http://www.gnu.org/licenses/ 
 */

package ca.nrc.cadc.wcs;

import ca.nrc.cadc.util.Log4jInit;
import org.apache.logging.log4j.*;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author pdowler
 */
public class SpatialTest
{
    private static final Logger log = LogManager.getLogger(SpatialTest.class);

    public SpatialTest()
    {
    }
    
    @Test
    public void testLinear()
    {
        try
        {
            WCSKeywords wcs = new WCSKeywordsImpl();
            wcs.put("COORDSYS", "ICRS");
            wcs.put("NAXIS", 2);
            //wcs.put("NAXIS1", 1000);
            //wcs.put("MAXIS2", 1000);
            wcs.put("CTYPE1", "RA");
            wcs.put("CTYPE2", "DEC");
            wcs.put("CUNIT1", "deg");
            wcs.put("CUNIT2", "deg");
            wcs.put("CRPIX1", 0.0);
            wcs.put("CRVAL1", 10.0);
            wcs.put("CRPIX2", 0.0);
            wcs.put("CRVAL2", 20.0);
            wcs.put("CD1_1", 1.0E-3);
            wcs.put("CD1_2", 0.0);
            wcs.put("CD2_1", 0.0);
            wcs.put("CD2_2", 1.0E-3);
            
            Transform trans = new Transform(wcs);
            
            log.info("testLinear WCS Transform: " + trans);
            
            double[] pix = new double[2];
            pix[0] = 500.0;
            pix[1] = 500.0;
            Transform.Result result = trans.pix2sky(pix);
            Assert.assertNotNull(result);
            Assert.assertNotNull(result.coordinates);
            Assert.assertEquals(2, result.coordinates.length);
            
            log.info("testLinear " + pix[0] + "," + pix[1] + " -> " + result.coordinates[0] + "," + result.coordinates[1]);
            Assert.assertEquals("RA center",  10.5, result.coordinates[0], 0.001);
            Assert.assertEquals("DEC center", 20.5, result.coordinates[1], 0.001);
            
        }
        catch(Exception unexpected)
        {
            log.error("unexpected exception", unexpected);
            Assert.fail("unexpected exception: " + unexpected);
        }
    }
    
    @Test
    public void testHPX()
    {
        try
        {
            // values from a JCMT scuba2 healpix product
            WCSKeywords wcs = new WCSKeywordsImpl();
            wcs.put("COORDSYS", "ICRS");
            wcs.put("NAXIS", 2);
            //wcs.put("NAXIS1", 1161);
            //wcs.put("MAXIS2", 1933);
            wcs.put("CTYPE1", "RA---HPX");
            wcs.put("CTYPE2", "DEC--HPX");
            wcs.put("CUNIT1", "deg");
            wcs.put("CUNIT2", "deg");
            wcs.put("CRPIX1", 139264.5);
            wcs.put("CRVAL1", 0.0);
            wcs.put("CRPIX2", 47003.5);
            wcs.put("CRVAL2", 0.0);
            wcs.put("CD1_1", -3.43322768917E-4);
            wcs.put("CD1_2", -3.43322768916E-4);
            wcs.put("CD2_1", -3.43322768917E-4);
            wcs.put("CD2_2", 3.43322768917E-4);
            
            Transform trans = new Transform(wcs);
            
            log.info("testHPX WCS Transform: " + trans);
            
            double[] pix = new double[2];
            pix[0] = 580.0; // centre
            pix[1] = 966.0; // centre
            Transform.Result result = trans.pix2sky(pix);
            Assert.assertNotNull(result);
            Assert.assertNotNull(result.coordinates);
            Assert.assertEquals(2, result.coordinates.length);
            
            log.info("testHPX " + pix[0] + "," + pix[1] + " -> " + result.coordinates[0] + "," + result.coordinates[1]);
            // expected values from manual use of astropy+wcslib-5.x
            Assert.assertEquals("RA center",  63.418925, result.coordinates[0], 0.001);
            Assert.assertEquals("DEC center", 28.113975, result.coordinates[1], 0.001);
            
        }
        catch(Exception unexpected)
        {
            log.error("unexpected exception", unexpected);
            Assert.fail("unexpected exception: " + unexpected);
        }
    }
}
