package com.gavagai.jussiutil;

import java.util.List;

public class TextWithSentiment {
	String text;
	Sentiment sentiment = null;
	String id;
	List<String> tokens;

	public List<String> getTokens() {
		return tokens;
	}

	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setSentiment(Sentiment s) {
		this.sentiment = s;
	}

	public Sentiment getSentiment() {
		return sentiment;
	}

	public TextWithSentiment(String a, String t) {
		this.text = t;
		this.sentiment = Sentiment.constrain(a);
	}

	TextWithSentiment(String t) {
		this.text = t;
	}

	public TextWithSentiment(Sentiment a, String t) {
		this.text = t;
		this.sentiment = a;
	}

	public String toString() {
		return this.sentiment+":"+this.text;
	}
}