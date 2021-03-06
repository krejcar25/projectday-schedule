package cz.krejcar25.projectday.schedule;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.InputStream;
import java.util.logging.Logger;

class XmlValidation
{
	private static final Logger log = Logger.getLogger(XmlValidation.class.getName());

	static boolean validateAgainstXSD(InputStream xml, InputStream xsd)
	{
		try
		{
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new StreamSource(xsd));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(xml));
			return true;
		}
		catch (Exception ex)
		{
			log.warning(ex.getMessage());
			return false;
		}
	}
}
