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

package org.amanzi.awe.gpeh.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


    /**
     * <p>
     * Time period wrapper
     * </p>
     * 
     * @author TsAr
     * @since 1.0.0
     */
    public  class GPEHFileNameWrapper {

        private int year;
        private int month;
        private int day;
        private int hhStart;
        private int mmStart;
        private int hhEnd;
        private int mmEnd;
        private int zStart;
        private int zEnd;
        Pattern time = Pattern.compile("(^A)(\\d{4})(\\d{2})(\\d{2})(\\.)(\\d{2})(\\d{2})([+-]{1}\\d{2})(\\d{2})(-)(\\d{2})(\\d{2})([+-]{1}\\d{2})(\\d{2})(_)(\\w+)(=)(\\w+)(\\,)(\\w+)(=)(\\S+)(\\.)(\\w+)(\\.)(.*$)");
        Pattern time2 = Pattern.compile("(^A)(\\d{4})(\\d{2})(\\d{2})(\\.)(\\d{2})(\\d{2})(-)(\\d{2})(\\d{2})(_)(\\w+)(=)(\\w+)(\\,)(\\w+)(=)(\\S+)(\\.)(\\w+)(\\.)(.*$)");
        private int minEnd;
        private int minStart;
        
        private String subNetwork;
        private String meContext;

        /**
         * Instantiates a new time period.
         * 
         * @param fileName the file name
         */
        public GPEHFileNameWrapper(String fileName) {
            Matcher matcher = time.matcher(fileName);
            if (matcher.matches()) {
                year = Integer.valueOf(matcher.group(2));
                month = Integer.valueOf(matcher.group(3));
                day = Integer.valueOf(matcher.group(4));
                hhStart = Integer.valueOf(matcher.group(6));
                mmStart = Integer.valueOf(matcher.group(7));
                String zone = matcher.group(8);
                if (zone.startsWith("+")) {
                    zone = zone.substring(1);
                }
                zStart = Integer.valueOf(zone);

                hhEnd = Integer.valueOf(matcher.group(11));
                mmEnd = Integer.valueOf(matcher.group(12));
                zone = matcher.group(13);
                if (zone.startsWith("+")) {
                    zone = zone.substring(1);
                }
                zEnd = Integer.valueOf(zone);
                minStart = (hhStart - zStart) * 60 + mmStart;
                minEnd = (hhEnd - zEnd) * 60 + mmEnd;
                
                setSubNetwork(matcher.group(18));
                setMeContext(matcher.group(22));
            }else {
                matcher = time2.matcher(fileName);  
                if (matcher.matches()){
                    year = Integer.valueOf(matcher.group(2));
                    month = Integer.valueOf(matcher.group(3));
                    day = Integer.valueOf(matcher.group(4));
                    hhStart = Integer.valueOf(matcher.group(6));
                    mmStart = Integer.valueOf(matcher.group(7));

                    zStart =0;

                    hhEnd = Integer.valueOf(matcher.group(9));
                    mmEnd = Integer.valueOf(matcher.group(10));

                    zEnd = 0;
                    minStart = (hhStart - zStart) * 60 + mmStart;
                    minEnd = (hhEnd - zEnd) * 60 + mmEnd;     
                    
                    setSubNetwork(matcher.group(14));
                    setMeContext(matcher.group(18));
                }  else {
                    hhStart = -1;
                }
            }
        }

        /**
         * Gets the year.
         *
         * @return the year
         */
        public int getYear() {
            return year;
        }

        /**
         * Gets the month.
         *
         * @return the month
         */
        public int getMonth() {
            return month;
        }

        /**
         * Gets the day.
         *
         * @return the day
         */
        public int getDay() {
            return day;
        }

        public static void main(String[] args) {
            Integer.valueOf("-02");
//            new GPEHTimeWrapper("A20100214.1200-0200-1215+0200_SubNetwork=ERNOR2,MeContext=ERNOR2_rnc_gpehfile_Mp1.bin.gz");
            new GPEHFileNameWrapper("A20101101.0200-0215_SubNetwork=ERNOR1,MeContext=ERNOR1_rnc_gpehfile_Mp0.bin.gz");
        }

        /**
         * Checks if is valid.
         * 
         * @return true, if is valid
         */
        public boolean isValid() {
            return hhStart >= 0;
        }

        /**
         * Gets the hh start.
         * 
         * @return the hh start
         */
        public int getHhStart() {
            return hhStart;
        }

        /**
         * Sets the hh start.
         * 
         * @param hhStart the new hh start
         */
        public void setHhStart(int hhStart) {
            this.hhStart = hhStart;
        }

        /**
         * Gets the mm start.
         * 
         * @return the mm start
         */
        public int getMmStart() {
            return mmStart;
        }

        /**
         * Sets the mm start.
         * 
         * @param mmStart the new mm start
         */
        public void setMmStart(int mmStart) {
            this.mmStart = mmStart;
        }

        /**
         * Gets the hh end.
         * 
         * @return the hh end
         */
        public int getHhEnd() {
            return hhEnd;
        }

        /**
         * Sets the hh end.
         * 
         * @param hhEnd the new hh end
         */
        public void setHhEnd(int hhEnd) {
            this.hhEnd = hhEnd;
        }

        /**
         * Gets the mm end.
         * 
         * @return the mm end
         */
        public int getMmEnd() {
            return mmEnd;
        }

        /**
         * Sets the mm end.
         * 
         * @param mmEnd the new mm end
         */
        public void setMmEnd(int mmEnd) {
            this.mmEnd = mmEnd;
        }

        /**
         * Gets the z start.
         * 
         * @return the z start
         */
        public int getzStart() {
            return zStart;
        }

        /**
         * Sets the z start.
         * 
         * @param zStart the new z start
         */
        public void setzStart(int zStart) {
            this.zStart = zStart;
        }

        /**
         * Gets the z end.
         * 
         * @return the z end
         */
        public int getzEnd() {
            return zEnd;
        }

        /**
         * Sets the z end.
         * 
         * @param zEnd the new z end
         */
        public void setzEnd(int zEnd) {
            this.zEnd = zEnd;
        }

        /**
         * Check date.
         * 
         * @param hour the hour
         * @param minute the minute
         * @param second the second
         * @param millisecond the millisecond
         * @return true, if successful
         */
        public boolean checkDate(long hour, long minute, long second, long millisecond) {
            long timeMin = hour * 60 + minute;
            return (minStart <= timeMin) && (minEnd > timeMin);
        }

		/**
		 * @param subNetwork the subNetwork to set
		 */
		public void setSubNetwork(String subNetwork) {
			this.subNetwork = subNetwork;
		}

		/**
		 * @return the subNetwork
		 */
		public String getSubNetwork() {
			return subNetwork;
		}

		/**
		 * @param meContext the meContext to set
		 */
		public void setMeContext(String meContext) {
			this.meContext = meContext;
		}

		/**
		 * @return the meContext
		 */
		public String getMeContext() {
			return meContext;
		}

    }
