package hr.fer.zemris.java.custom.scripting.demo;

import hr.fer.oprpp1.custom.scripting.nodes.DocumentNode;
import hr.fer.oprpp1.custom.scripting.nodes.EchoNode;
import hr.fer.oprpp1.custom.scripting.nodes.ForLoopNode;
import hr.fer.oprpp1.custom.scripting.nodes.INodeVisitor;
import hr.fer.oprpp1.custom.scripting.nodes.TextNode;
import hr.fer.oprpp1.custom.scripting.parser.SmartScriptParser;

public class TreeWriter {
	public static void main(String[] args) {
//		if(args.length != 1)
//			throw new IllegalArgumentException("Expecting only 1 argument, file name!");
//		
//		Path path = Paths.get(args[0]); 
		
		/*String docBody = "This is sample text.\r\n"
				+ "{$ FOR i 1 10 1 $}\n"
				+ "This is {$= i $}-th time this message is generated.\n"
				+ "{$END$}\n"
				+ "{$FOR i 0 10 2 $}\n"
				+ "sin({$=i$}^2) = {$= i i * @sin \"0.000\" @decfmt $}\n"
				+ "{$END$}\n"
				+ "";*/
		String docBody = "{$= \"colorChanged\" \"The color was not changed\" @tparamGet$}";
//		try {
//			docBody = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		System.out.println(docBody);
		System.out.println("================================");
		SmartScriptParser p = new SmartScriptParser(docBody);
		WriterVisitor visitor = new WriterVisitor();
		p.getDocumentNode().accept(visitor);
		// by the time the previous line completes its job, the document should have been
		// written
		// on the standard output
	}
	
	private static class WriterVisitor implements INodeVisitor{

		private StringBuilder sb = new StringBuilder();

		@Override
		public void visitTextNode(TextNode node) {
			String text = node.getText();

			for (int i = 0; i < text.length(); i++) {
				char ch = text.charAt(i);
				if (ch == '{' || ch == '\\') {
					sb.append('\\');
				}

				sb.append(ch);
			}
		}

		@Override
		public void visitForLoopNode(ForLoopNode node) {
			sb.append(node.toString());

			if(node.getCol() != null) {
				for(int i = 0; i < node.numberOfChildren(); i++) {
					node.getChild(i).accept(this);
				}
			}

			sb.append("{$END$}");
		}

		@Override
		public void visitEchoNode(EchoNode node) {
			sb.append(node.toString());
		}

		@Override
		public void visitDocumentNode(DocumentNode node) {
			int i = 0;
			int numOfChildren = node.numberOfChildren();
			while (i < numOfChildren) {
				node.getChild(i).accept(this);
				i++;
			}

			System.out.println(sb.toString());
		}
		
	}
}
