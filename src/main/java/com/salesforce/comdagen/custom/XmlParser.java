package com.salesforce.comdagen;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.tuple.Pair;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XmlParser {
    public static void main(String[] args) throws Exception {

        File dir = new File("/Users/pnaskar/dev/git-repos/comdagen/output/generated/pricebooks"); //Directory where your file exists

        File [] files = dir.listFiles();

        Set<String> allPriceEntries = new HashSet<>();
        long numberOfPriceEntries = 0L;

        Path path = Paths.get("price-entry.txt");
        if (Files.exists( path )) {
            Files.delete( path );
        }
        Files.createFile( path );

        for(File file : files) {
            if(file.isFile() && file.getName().endsWith(".xml")) { //You can validate file name with extension if needed
                Pair<Set<String>, Long> priceEntries = processFile(file);
                allPriceEntries.addAll( priceEntries.getLeft() );
                numberOfPriceEntries += priceEntries.getRight();
            }
        }

        Files.write( Paths.get("price-entry.txt"), allPriceEntries.toString().getBytes());

        System.out.println("Total price entries : " + numberOfPriceEntries );
        System.out.println("Number of unique price entries : " + allPriceEntries.size() );
        System.out.println("Percent of unique price entries : " + ((double)allPriceEntries.size()/(double)numberOfPriceEntries) * 100.0 );
    }

    private static Pair<Set<String>, Long> processFile( File fXmlFile) {
        Set<String> priceEntries = new HashSet<>();
        long totalPriceEntries = 0L;

        try {
            System.out.println(fXmlFile.getName());
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(fXmlFile);
            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();
            XPathExpression expr = xpath.compile("/pricebooks/pricebook/price-tables/price-table/amount/text()");
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            totalPriceEntries = nodes.getLength();
            for (int i = 0; i < nodes.getLength(); i++) {
                priceEntries.add(nodes.item(i).getNodeValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Pair.of( priceEntries,  totalPriceEntries);
    }
}
