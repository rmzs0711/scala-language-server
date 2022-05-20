package requests;

import parser.Parser;
import parser.SimpleNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileInformation {
    private final String path;
    private boolean isChanged;
    private List<DefinitionVariable> variables;
    private List<DefinitionFunction> functions;
    private SimpleNode root;

    FileInformation(String path) {
        this.path = path;
        isChanged = true;
        variables = new ArrayList<>();
        functions = new ArrayList<>();
        root = null;
        update();
    }

    public List<DefinitionVariable> getVariables() {
        return variables;
    }

    public List<DefinitionFunction> getFunctions() {
        return functions;
    }

    public FileInformation update() {
        if (isChanged) {
            variables.clear();
            functions.clear();
            isChanged = false;

            root = Parser.parse(path);
            if (root != null) {
                root = (SimpleNode) root.jjtGetChild(0);

                for (int i = 0; i < root.jjtGetNumChildren(); i++) {
                    if (Objects.equals(root.jjtGetChild(i).toString(), "Statement")) {
                        if (Objects.equals(root.jjtGetChild(i).jjtGetChild(0).toString(), "VariableDeclarationStatement")) {
                            SimpleNode statement = (SimpleNode) root.jjtGetChild(i).jjtGetChild(0);
                            for (int j = 0; j < statement.jjtGetNumChildren(); j++) {
                                if (Objects.equals(statement.jjtGetChild(j).toString(), "VariableDeclaration")) {
                                    variables.add(new DefinitionVariable((SimpleNode) statement.jjtGetChild(j)));
                                }
                            }
                        }
                        else if (Objects.equals(root.jjtGetChild(i).jjtGetChild(0).toString(), "ProcedureDeclarationStatement")) {
                            functions.add(new DefinitionFunction((SimpleNode) root.jjtGetChild(i).jjtGetChild(0)));
                        }
                    }
                }
            }
        }
        return this;
    }
}