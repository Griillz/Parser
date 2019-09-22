package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Parser {

	static String token;
	static String tempNext;
	static File tokenFile = new File("tokens");
	static Scanner sc = null;
	static String[] tokens;
	static int numTok = -1; 
	static String lexString = "";
	static int o = 0;
	static Node root;

	public static void main(String args[]) {
		
		File file = new File(args[0]);

		try {
			Lexer lexer = new Lexer(file);
			sc = new Scanner(tokenFile);
			while(!lexString.equals("$")) {
				numTok++;
				lexString = sc.nextLine();
			}
			tokens = new String[numTok + 1];
			sc = new Scanner(tokenFile);
			for(int i = 0; i < tokens.length; i++) {
				tokens[i] = sc.nextLine();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		token = tokens[o];
		
		root = new Node("program");
		root.addChild(declarationList());
		if (token.equals("$"))
			System.out.println("ACCEPT");
		else {
			rej();
		}
		
		//System.out.println("HERE@@@@@@@@@@@@@@");
		//root.printChildren(root);
		//System.out.println("END OF PARSE TREE");
		

	}
	
	private static void nextToken() {
		o++;
		token = tokens[o];
	}

	private static Node declarationList() {
		//System.out.println("DECLIST " + token);
		Node node = new Node("declarationList");
		node.addChild(declaration());
		node.addChild(declarationListP());
		
		return node;
	}

	private static Node declarationListP() {
		Node node = new Node("declarationListP");
		//System.out.println("DECLISTP " + token);
		if(token.equals("K: int") || token.equals("K: void")) {
		node.addChild(declaration());
		node.addChild(declarationListP());
		}
		else {
			node.addChild(epsilon());
		}
		
		return node;
	}

	private static Node declaration() {
		Node node = new Node("declaration");
		//System.out.println("DEC " + token);
		node.addChild(typeSpecifier());
		if (token.contains("ID: ")) {
			//System.out.println("DEC " + token);
			node.addChild(token());
			nextToken();
		} else {
			rej();
		}

		node.addChild(declarationP());
		
		return node;
	}

	private static Node declarationP() {
		Node node = new Node("declarationP");
		//System.out.println("DECP " + token);
		if (token.equals(";") || token.equals("[")) {
			node.addChild(varDeclarationP());
		} else {
			if (token.equals("(")) {
				node.addChild(functionDeclarationP());
			} else {
				rej();
			}
		}
		
		return node;
	}

	private static Node varDeclaration() {
		Node node = new Node("varDeclaration");
		//System.out.println("VARDEC " + token);
		node.addChild(typeSpecifier());
		//System.out.println("VARDEC AFTER TYPE " + token);
		if (token.contains("ID: ")) {
			node.addChild(token());
			nextToken();
			if (token.equals(";")) {
				node.addChild(token());
				nextToken();
			} else if (token.equals("[")) {
				node.addChild(token());
				nextToken();
				if (token.contains("NUM: ")) {
					node.addChild(token());
					nextToken();

					if (token.equals("]")) {
						node.addChild(token());
						nextToken();
						if (token.equals(";")) {
							node.addChild(token());
							nextToken();
						} else {
							rej();
						}
					} else {
						rej();
					}
				} else {
					rej();
				}
			} else {
				rej();
			}
		} else {
			rej();
		}
		
		return node;

	}
		 

	private static Node varDeclarationP() {
		Node node = new Node("varDeclarationP");
		//System.out.println("VARDECP " + token);
		if (token.equals(";")) {
			node.addChild(token());
			nextToken();
		}
		else if (token.equals("[")) {
			node.addChild(token());
			nextToken();
			if (token.contains("NUM: ")) {
				node.addChild(token());
				nextToken();
				if (token.equals("]")) {
					node.addChild(token());
					nextToken();
					if (token.equals(";")) {
						node.addChild(token());
						nextToken();
					} else {
						rej();
					}
				} else {
					rej();
				}
			} else {
				rej();
			}
		} else {
			rej();
		}
		
		return node;
	}

	private static Node typeSpecifier() {
		Node node = new Node("typeSpecifier");
		//System.out.println(node.getData() + "HELLO");
		//System.out.println("TYPESPEC " + token);
		if (token.equals("K: int") || token.equals("K: void")) {
			node.addChild(token());
			nextToken();
		} else {
			rej();
		}
		
		return node;
	}

	private static Node functionDeclarationP() {
		Node node = new Node("functionDeclarationP");
		if (token.equals("(")) {
			node.addChild(token());
			nextToken();
			node.addChild(params());
			if (token.equals(")")) {
				node.addChild(token());
				nextToken();
			} else {
				rej();
			}
			node.addChild(compoundStatement());
		}
		else {
			rej();
		}
		
		
		return node;
	}

	private static Node params() {
		Node node = new Node("params");
		//System.out.println("PARAMS " + token);
		if (token.equals("K: void")) {
			node.addChild(token());
			nextToken();
		} else
			node.addChild(paramsList());
		
		return node;
	}

	private static Node paramsList() {
		Node node = new Node("paramList");
		//System.out.println("PARAMSLIST " + token);
		node.addChild(param());
		node.addChild(paramsListP());
		
		return node;
	}

	private static Node paramsListP() {
		Node node = new Node("paramListP");
		//System.out.println("PARAMSLISTP " + token);
		if (token.equals(",")) {
			node.addChild(token());
			nextToken();
			node.addChild(param());
			node.addChild(paramsListP());
		}
		else {
			node.addChild(epsilon());
		}
		
		return node;
	}

	private static Node param() {
		Node node = new Node("param");
		//System.out.println("PARAM " + token);
		node.addChild(typeSpecifier());
		if(token.contains("ID: ")) {
			node.addChild(token());
			nextToken();
			if(token.equals("[")) {
				node.addChild(token());
				nextToken();
				if(token.equals("]")) {
					node.addChild(token());
					nextToken();
				}
				else {
					rej();
				}
			}
		}
		else {
			rej();
		}
		
		return node;
	}

	private static Node compoundStatement() {
		Node node = new Node("compoundStatement");
		//System.out.println("COMPSTMT " + token);
		if (token.equals("{")) {
			node.addChild(token());
			nextToken();
		}
		else {
			rej();
		}
		node.addChild(localDeclaration());
		node.addChild(statementList());
		//System.out.println("COMPSTMT " + token);
		if (token.equals("}")) {
			node.addChild(token());
			nextToken();
		}
		else {
			//System.out.println("THIS ONE");
			rej();
		}
		
		return node;
	}

	private static Node localDeclaration() {
		Node node = new Node("localDelcaration");
		//System.out.println("LOCDEC " + token);
		if (token.contains("K: int") || token.contains("K: void")) {
			node.addChild(varDeclaration());
			node.addChild(localDeclaration());
		}
		else {
			node.addChild(epsilon());
		}
		
		return node;
	}

	private static Node statementList() {
		Node node = new Node("statementList");
		//System.out.println("STMTLISTP " + token);
		if (token.contains("K: ") || token.equals(";") || token.contains("NUM: ") || token.equals("(") || token.contains("ID: ") || token.equals("{"))  {
			node.addChild(statement());
			node.addChild(statementList());
		}
		else {
			node.addChild(epsilon());
		}
		
		return node;
	}


	private static Node statement() {
		Node node = new Node("statement");
		//System.out.println("STMT " + token);
		if(token.equals("{"))
			node.addChild(compoundStatement());
		else if(token.equals("K: if"))
			node.addChild(selectionStatement());
		else if(token.equals("K: while"))
			node.addChild(iterationStatement());
		else if(token.equals("K: return"))
			node.addChild(returnStatement());
		else
			node.addChild(expressionStatement());
		
		return node;

	}

	private static Node expressionStatement() {
		Node node = new Node("expressionStatement");
		//System.out.println("EXPSTMT " + token);
		if (token.equals(";")) {
			node.addChild(token());
			nextToken();
		} else {
			node.addChild(expression());
			if (token.equals(";")) {
				node.addChild(token());
				nextToken();
			} else {
				rej();
			}
		}
		
		return node;
	}

	private static Node selectionStatement() {
		Node node = new Node("selectionStatement");
		//System.out.println("SELSTMT " + token);
		if (token.equals("K: if")) {
			node.addChild(token());
			nextToken();
			if (token.equals("(")) {
				node.addChild(token());
				nextToken();
				node.addChild(expression());
				if (token.equals(")")) {
					node.addChild(token());
					nextToken();
					node.addChild(statement());
					if (token.equals("K: else")) {
						node.addChild(token());
						nextToken();
						node.addChild(statement());
					}
				} else {
					
				}
			} else {
				rej();
			}
		} else {
			rej();
		}
		
		return node;
	}

	private static Node iterationStatement() {
		Node node = new Node("iterationStatement");
		//System.out.println("ITERSTMT " + token);
		if (token.equals("K: while")) {
			node.addChild(token());
			nextToken();
			if (token.equals("(")) {
				node.addChild(token());
				nextToken();
				node.addChild(expression());
				if (token.equals(")")) {
					node.addChild(token());
					nextToken();
					node.addChild(statement());
				} else {
					rej();
				}

			} else {
				rej();
			}
		} else {
			rej();
		}
		
		return node;
	}

	private static Node returnStatement() {
		Node node = new Node("returnStatement");
		//System.out.println("RETSTMT " + token);
		if (token.equals("K: return")) {
			node.addChild(token());
			nextToken();
			if(token.equals("(") || token.contains("NUM: ") || token.contains("ID: ")) {
				node.addChild(expression());
			}
			if (token.equals(";")) {
				node.addChild(token());
				nextToken();
			}
			else
				rej();
		}
		else
			rej();
		
		return node;
	}

	private static Node expression() {
		Node node = new Node("expression");
		//System.out.println("EXP " + token);
		node.addChild(additiveExpression());
		if(token.equals("<=") || token.equals("<") || token.equals(">") || token.equals(">=") || 
				token.equals("==") || token.equals("!=")) {
			node.addChild(relop());
			node.addChild(additiveExpression());
		}
		
		return node;
	}

	private static Node var() {
		Node node = new Node("var");
		//System.out.println("VAR " + token);
		if(token.equals("[")) {
			node.addChild(token());
			nextToken();
			node.addChild(expression());
			if(token.equals("]")) {
				node.addChild(token());
				nextToken();
			}
			else {
				rej();
			}
		}
		if(token.equals("=")) {
			node.addChild(token());
			nextToken();
			boolean done = false;
			if(token.equals("[")) {
				node.addChild(token());
				nextToken();
				node.addChild(expression());
				done = true;
				if(token.equals("]")) {
					node.addChild(token());
					nextToken();
				}
				else {
					rej();
				}
			}
			if(!done) {
				node.addChild(expression());
			}
			
		}
		else {
			node.addChild(epsilon());
		}
		
		return node;
	}


	private static Node relop() {
		Node node = new Node("relop");
		//System.out.println("RELOP " + token);
		if (token.equals("<=") || token.equals("<") || token.equals(">") || token.equals(">=") || token.equals("==")
				|| token.equals("!=")) {
			node.addChild(token());
			nextToken();
		}
		else {
			rej();
		}
		
		return node;
	}

	

	private static Node additiveExpression() {
		Node node = new Node("additiveExpression");
		//System.out.println("ADDEXP " + token);
		node.addChild(term());
		node.addChild(additiveExpressionP());
		
		return node;
	}

	private static Node additiveExpressionP() {
		Node node = new Node("additiveExpressionP");
		//System.out.println("ADDEXPP " + token);
		if(token.equals("+") || token.equals("-")) {
			node.addChild(addop());
			node.addChild(term());
			node.addChild(additiveExpressionP());
		}
		else {
			node.addChild(epsilon());
		}
		
		return node;
	}

	private static Node addop() {
		Node node = new Node("addop");
		//System.out.println("ADDOP " + token);
		if(token.equals("+") || token.equals("-")) {
			node.addChild(token());
			nextToken();
		}
		else {
			rej();
		}
		
		return node;
	}

	private static Node term() {
		Node node = new Node("term");
		//System.out.println("TERM " + token);
		node.addChild(factor());
		node.addChild(termP());
		
		return node;
	}

	private static Node termP() {
		Node node = new Node("termP");
		//System.out.println("TERMP " + token);
		if(token.equals("*") || token.equals("/")) {
			node.addChild(mulop());
			node.addChild(factor());
			node.addChild(termP());
		}
		else {
			node.addChild(epsilon());
		}
		
		return node;
	}

	private static Node mulop() {
		Node node = new Node("mulop");
		//System.out.println("MULOP " + token);
		if(token.equals("*") || token.equals("/")) {
			node.addChild(token());
			nextToken();
		}
		else {
			rej();
		}
		
		return node;
	}

	private static Node factor() {
		Node node = new Node("factor");
		//System.out.println("FACTOR " + token);
		if (token.equals("(")) {
			node.addChild(token());
			nextToken();
			node.addChild(expression());
			if (token.equals(")")) {
				node.addChild(token());
				nextToken();
			} else {
				rej();
			}
		}
		else if (token.contains("ID: ")) {
			node.addChild(token());
			nextToken();
			node.addChild(factorP());
		}

		else if (token.contains("NUM: ")) {
			node.addChild(token());
			nextToken();
		} else {
			rej();
		}
		
		return node;
	}

	private static Node factorP() {
		Node node = new Node("factorP");
		//System.out.println("FACTORP " + token);
		if(token.equals("(") || token.equals(",")) {
			node.addChild(callP());
		}
		else
			node.addChild(var());
		
		return node;
		}
	
	


	private static Node callP() {
		Node node = new Node("callP");
		//System.out.println("CALLP " + token);
		if(token.equals("(")) {
			node.addChild(token());
			nextToken();
			node.addChild(args());
			if(token.equals(")")) {
				node.addChild(token());
				nextToken();
			}
			else {
				rej();
			}
		} else if (token.equals(",")) {
			node.addChild(args());
		}
		else {
			rej();
		}
		
		return node;
	}

	private static Node args() {
		Node node = new Node("args");
		//System.out.println("ARGS " + token);
		if(token.equals("(") || token.contains("ID: ") || token.contains("NUM: ")) {
			node.addChild(argList());
		}
		else if(token.equals(",")) {
			node.addChild(argListP());
		}
		else {
			node.addChild(epsilon());
		}
		return node;
	}

	private static Node argList() {
		Node node = new Node("argList");
		//System.out.println("ARGLIST " + token);
		node.addChild(expression());
		node.addChild(argListP());
		
		return node;
	}

	private static Node argListP() {
		Node node = new Node("argListP");
		//System.out.println("ARGLISTP " + token);
		if(token.equals(",")) {
			node.addChild(token());
			nextToken();
			node.addChild(expression());
			node.addChild(argListP());
		}
		else {
			node.addChild(epsilon());
		}
		
		return node;
	}

	private static void rej() {
		System.out.println("REJECT");
		System.exit(0);
	}
	
	private static Node token() {
		Node node = new Node(token);
		return node;
	}
	
	private static Node epsilon() {
		Node node = new Node("Epsilon");
		return node;
	}

}
