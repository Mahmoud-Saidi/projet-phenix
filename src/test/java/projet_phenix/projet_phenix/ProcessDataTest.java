package projet_phenix.projet_phenix;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

public class ProcessDataTest {
	
	File file;
	
	@Before
	public void init() {
		
		ClassLoader classLoader = getClass().getClassLoader();
		file = new File(classLoader.getResource("transactions_20170518.data").getFile());
	}

	@Test
	public void testReadFile() throws FileNotFoundException {
		
		Stream<String[]> resStream = ProcessData.readFile(file.getAbsolutePath());

		assertEquals(resStream.count(),4);
		
	}
	
	@Test
	public void testGetData() throws IOException {
		
		Stream<String[]> resStream = ProcessData.getData(file.getParent(), "20170518", 2);

		assertEquals(resStream.count(),11);
		
	}

}
