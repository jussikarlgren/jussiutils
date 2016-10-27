package com.gavagai.jussiutil;

public class TextWithTone {
	String text;
	Tone sentiment = null;

	public String getText() {
		return text;
	}

	public void setSentiment(Tone s) {
		this.sentiment = s;
	}

	public Tone getSentiment() {
		return sentiment;
	}

	public TextWithTone(String a, String t) {
		this.text = t;
		this.sentiment = Tone.translate(a);
	}

	public TextWithTone(String t) {
		this.text = t;
	}

	public TextWithTone(Tone a, String t) {
		this.text = t;
		this.sentiment = a;
	}
}

