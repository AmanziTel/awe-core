package org.amanzi.awe.neighbours.legend;

public enum LegendElements {
	OTHERS {
		@Override
		public LegendRelationsIcons getElementIcon() {
			return LegendRelationsIcons.OTHERS;
		}


		@Override
		public String getDescription() {
			return " <= 0.002";
		}
	},
	MORE_0_2{
		@Override
		public LegendRelationsIcons getElementIcon() {
			return LegendRelationsIcons.MORE_0_2;
		}



		@Override
		public String getDescription() {
			return " <= 0.01";
		}
	},
	MORE_1{
		@Override
		public LegendRelationsIcons getElementIcon() {
			return LegendRelationsIcons.MORE_1;
		}



		@Override
		public String getDescription() {
			return " <= 0.05";
		}
	},
	MORE_5{
		@Override
		public LegendRelationsIcons getElementIcon() {
			return LegendRelationsIcons.MORE_5;
		}


		@Override
		public String getDescription() {
			return " <= 0.11";
		}       	
	},
	MORE_15{
		@Override
		public LegendRelationsIcons getElementIcon() {
			return LegendRelationsIcons.MORE_15;
		}



		@Override
		public String getDescription() {
			return " <= 0.30";
		}       	
	},
	MORE_30{
		@Override
		public LegendRelationsIcons getElementIcon() {
			return LegendRelationsIcons.MORE_30;
		}



		@Override
		public String getDescription() {
			return " <= 0.50";
		}       	
	},
	MORE_50{
		@Override
		public LegendRelationsIcons getElementIcon() {
			return LegendRelationsIcons.MORE_50;
		}



		@Override
		public String getDescription() {
			return " > 0.50";
		}       	
	};

	public abstract String getDescription();
	public abstract LegendRelationsIcons getElementIcon();

}
