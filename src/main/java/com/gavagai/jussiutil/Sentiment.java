package com.gavagai.jussiutil;

public enum Sentiment {
	POSITIVE, NEGATIVE, NEUTRAL, LOVE, HATE, SEXY, SKEPTIC, FEAR, BORING, VIOLENT, PROFANITY, ADHOMINEM, QUESTION, SURPRISE, DISAPPOINTMENT, OTHER;

	public static Sentiment translate(String attitude) {
		for (Sentiment p : Sentiment.values()) {
			if (attitude.equals(p.toString())) {
				return p;
			}
			if (attitude.equals("\""+p.toString()+"\"")) {
				return p;
			}
		}
		if (attitude.equals("pos")) {
			return Sentiment.POSITIVE;
		}
		if (attitude.equals("neg")) {
			return Sentiment.NEGATIVE;
		}
		return Sentiment.NEUTRAL;
	}

	public static Sentiment constrain(String poleName) {
		if (poleName.toLowerCase().startsWith("pos")) {
			return Sentiment.POSITIVE;
		}
		if (poleName.toLowerCase().startsWith("neg")) {
			return Sentiment.NEGATIVE;
		}
		if (poleName.toLowerCase().startsWith("neu")) {
			return Sentiment.NEUTRAL;
		}
		if (poleName.toLowerCase().startsWith("bang")) {
			return Sentiment.VIOLENT;
		}
		if (poleName.toLowerCase().startsWith("viol")) {
			return Sentiment.VIOLENT;
		}
		if (poleName.toLowerCase().startsWith("sex")) {
			return Sentiment.SEXY;
		}
		if (poleName.toLowerCase().startsWith("fear")) {
			return Sentiment.FEAR;
		}
		if (poleName.toLowerCase().startsWith("bor")) {
			return Sentiment.BORING;
		}
		if (poleName.toLowerCase().startsWith("love")) {
			return Sentiment.LOVE;
		}
		if (poleName.toLowerCase().startsWith("hate")) {
			return Sentiment.HATE;
		}
		if (poleName.toLowerCase().startsWith("iffy")) {
			return Sentiment.SKEPTIC;
		}
		if (poleName.toLowerCase().startsWith("uncert")) {
			return Sentiment.SKEPTIC;
		}
		if (poleName.toLowerCase().startsWith("ske")) {
			return Sentiment.SKEPTIC;
		}
		if (poleName.toLowerCase().startsWith("prof")) {
			return Sentiment.PROFANITY;
		}
		return Sentiment.OTHER;
	}
}
