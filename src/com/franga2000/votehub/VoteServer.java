package com.franga2000.votehub;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.xml.bind.DatatypeConverter;

public class VoteServer {
	public String name;
	public String address;
	public String custom;
	public int port;
	public PublicKey pubkey;
	
	public VoteServer(String name, String pubkey, String address, int port, String custom) throws InvalidKeySpecException, NoSuchAlgorithmException {
		byte[] encodedPublicKey = DatatypeConverter.parseBase64Binary(pubkey);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
		this.pubkey = keyFactory.generatePublic(publicKeySpec);
		this.address = address;
		this.port = port;
		this.name = name;
		this.custom = custom;
	}
}