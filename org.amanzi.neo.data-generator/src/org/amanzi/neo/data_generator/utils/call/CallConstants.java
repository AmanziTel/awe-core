/* AWE - Amanzi Wireless Explorer
 * http://awe.amanzi.org
 * (C) 2008-2009, AmanziTel AB
 *
 * This library is provided under the terms of the Eclipse Public License
 * as described at http://www.eclipse.org/legal/epl-v10.html. Any use,
 * reproduction or distribution of the library constitutes recipient's
 * acceptance of this agreement.
 *
 * This library is distributed WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.amanzi.neo.data_generator.utils.call;

/**
 * <p>
 * Constants for calls.
 * </p>
 * @author Shcharbatsevich_A
 * @since 1.0.0
 */
public class CallConstants {
    
    public static final int CALL_DURATION_PERIODS_COUNT = 8;
    
    public static final float[] IND_CALL_DURATION_BORDERS = new float[]{0.01f,1.25f,2.5f,3.75f,5,7.5f,10,12.5f,45,1000};
    public static final float[] IND_AUDIO_QUAL_BORDERS = new float[]{-0.5f,4.5f};
    public static final int[] IND_AUDIO_DELAY_BORDERS = new int[]{1,1000};
    public static final float IND_CALL_DURATION_TIME = 60;
    
    public static final float[] GR_CALL_DURATION_BORDERS = new float[]{0.01f,0.125f,0.25f,0.375f,0.5f,0.75f,1,2,5,1000};
    public static final float[] GR_AUDIO_QUAL_BORDERS = new float[]{-0.5f,4.5f};
    public static final int[] GR_AUDIO_DELAY_BORDERS = new int[]{1,1000};
    public static final float GR_CALL_DURATION_TIME = 20;
    
    public static final Long[] ITSI_DURATION_BORDERS = new Long[]{10L,CallGeneratorUtils.MILLISECONDS*50L};

}
