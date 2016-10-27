package com.gavagai.jussiutil;

public enum Tone {
	POSITIVITY, NEGATIVITY, NEUTRAL, LOVE, HATE, DESIRE, SKEPTICISM, FEAR, BORING, VIOLENCE, OTHER, PROFANITY, SURPRISE;

	public static String[] labels() {
		String[] l = new String[Tone.values().length];
		int i = 0;
		for (Tone p : Tone.values()) {
			l[i] = p.name();
			i++;
		}
		return(l);
	}
	public static Tone translate(String attitude) {
		for (Tone p : Tone.values()) {
			if (attitude.equals(p.toString())) {
				return p;
			}
			if (attitude.equals("\""+p.toString()+"\"")) {
				return p;
			}
		}
		if (attitude.equals("pos")) {
			return Tone.POSITIVITY;
		}
		if (attitude.equals("neg")) {
			return Tone.NEGATIVITY;
		}
		if (attitude.equals("POSITIVE")) {
			return Tone.POSITIVITY;
		}
		if (attitude.equals("NEGATIVE")) {
			return Tone.NEGATIVITY;
		}
		if (attitude.equals("positive")) {
			return Tone.POSITIVITY;
		}
		if (attitude.equals("negative")) {
			return Tone.NEGATIVITY;
		}
		return Tone.OTHER;
	}

	public static Tone constrain(String poleName) {
		if (poleName.toLowerCase().startsWith("pos")) {
			return Tone.POSITIVITY;
		}
		if (poleName.toLowerCase().startsWith("neg")) {
			return Tone.NEGATIVITY;
		}
		if (poleName.toLowerCase().startsWith("neu")) {
			return Tone.NEUTRAL;
		}
		if (poleName.toLowerCase().startsWith("bang")) {
			return Tone.VIOLENCE;
		}
		if (poleName.toLowerCase().startsWith("viol")) {
			return Tone.VIOLENCE;
		}
		if (poleName.toLowerCase().startsWith("sex")) {
			return Tone.DESIRE;
		}
		if (poleName.toLowerCase().startsWith("fear")) {
			return Tone.FEAR;
		}
		if (poleName.toLowerCase().startsWith("bor")) {
			return Tone.BORING;
		}
		if (poleName.toLowerCase().startsWith("love")) {
			return Tone.LOVE;
		}
		if (poleName.toLowerCase().startsWith("hate")) {
			return Tone.HATE;
		}
		if (poleName.toLowerCase().startsWith("iffy")) {
			return Tone.SKEPTICISM;
		}
		if (poleName.toLowerCase().startsWith("uncert")) {
			return Tone.SKEPTICISM;
		}
		if (poleName.toLowerCase().startsWith("ske")) {
			return Tone.SKEPTICISM;
		}
		if (poleName.toLowerCase().startsWith("profa")) {
			return Tone.PROFANITY;
		}
		if (poleName.toLowerCase().startsWith("surpr")) {
			return Tone.SURPRISE;
		}
		return Tone.OTHER;
	}
}
