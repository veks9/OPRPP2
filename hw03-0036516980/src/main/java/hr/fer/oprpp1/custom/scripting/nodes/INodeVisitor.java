package hr.fer.oprpp1.custom.scripting.nodes;

public interface INodeVisitor {
	public void visitTextNode(TextNode node);

	public void visitForLoopNode(ForLoopNode node);

	public void visitEchoNode(EchoNode node);

	public void visitDocumentNode(DocumentNode node);
}