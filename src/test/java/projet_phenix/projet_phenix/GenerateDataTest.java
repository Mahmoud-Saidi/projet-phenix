package projet_phenix.projet_phenix;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class GenerateDataTest {
	
	
	@Test
	public void testGetRandomString() {
		
		String res = GenerateData.getRandomString(5);
		assertEquals(res.length(),5);
		
	}
	
	@Test
	public void testGetRandomMagasinId() {
		
		String res = GenerateData.getRandomMagasinId();
		assertEquals(res.length(),36);
		
	}
	
	
	

}
