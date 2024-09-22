import java.io.*;
import java.util.Objects;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class ControleDeGastos {
    static boolean funcionando = true;
	static int ID;

	public static String lista(String caminhoDoArquivo) {
		StringBuilder lista;
		lista = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(caminhoDoArquivo))) {
			var linha = reader.readLine();
			while ((linha = reader.readLine()) != null) {
				{
					lista.append(linha).append("\n");
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("Arquivo " + caminhoDoArquivo + " não encontrado.");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return lista.toString();
	}




	public static int aleatorizarID () {
		ID = (int)(Math.random()*8999999);
		ID += 1000000;
		try (BufferedReader reader = new BufferedReader(new FileReader("ganhos.txt"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(Integer.toString(ID))) {
					ID = (int)(Math.random()*8999999);
				}
				else break;
			}
		} catch (FileNotFoundException _) {
			;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		try (BufferedReader reader = new BufferedReader(new FileReader("gastos.txt"))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(Integer.toString(ID))) {
					ID = (int)(Math.random()*8999999);
				} else break;
			}
		} catch (FileNotFoundException _) {
			;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} return ID;
	}

	public static String[] estatisticas(String caminhoDoArquivo) {
		float valorTotal = 0;
		int contadorDeLinhas = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader(caminhoDoArquivo))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains("Ganhos: ") || line.contains("Gastos: ")) {
					String[] arrayValor = line.split(" ");
					valorTotal += Float.parseFloat(arrayValor[1].replace(",","."));
				}
				contadorDeLinhas++;
			}
		} catch (FileNotFoundException e) {
			System.out.println("Arquivo " + caminhoDoArquivo + " não encontrado.");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String[] valorELinhas = new String[3];
		valorELinhas[0] = String.valueOf(valorTotal);
		valorELinhas[1] = String.valueOf(Math.round((float) contadorDeLinhas / 7));
		valorELinhas[2] = String.valueOf(valorTotal/(Math.round((float) contadorDeLinhas / 7)));
		return valorELinhas;
	}



	public static void removerID(String caminhoDoArquivo, String IDDeBusca) {
		File tempFile = new File("tempFile.txt");
		boolean renomeado = false;
		boolean found = false;
		try (BufferedReader reader = new BufferedReader(new FileReader(caminhoDoArquivo));
		     BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
			String line;
			int skipCount = 0;
			while ((line = reader.readLine()) != null) {
				if (skipCount > 0) {
					skipCount--;
					continue;
				}
				if (line.contains(IDDeBusca) && ((!line.contains("Gastos: ")) || (!line.contains("Descrição: ")) || (!line.contains("Ganhos: ")))) {
					System.out.println("Foi encontrado o ID " + IDDeBusca + " no arquivo " + caminhoDoArquivo + ".");
					found = true;
					skipCount = 6;
					continue;
				}
				writer.write(line + System.lineSeparator());
			}
			if (!found) {
				System.out.println("Não foi encontrado o ID " + IDDeBusca + " em " + caminhoDoArquivo + ".");
			}
		} catch (IOException e) {
			System.out.println("Erro de IOException!");
		}
		boolean arquivoRemovido = new File(caminhoDoArquivo).delete();
		if (arquivoRemovido) {
			renomeado = tempFile.renameTo(new File(caminhoDoArquivo));
		}
		if (!renomeado) {
			System.out.println("Erro ao renomear o arquivo temporário.");
		}
	}



    public static void switchCaseOperacao (String entrada) throws IOException {
        switch (entrada) {
	        case "sair":
		        funcionando = false;
		        break;


	        case "lista":
		        System.out.println("\nGanhos: \n");
		        System.out.println(lista("ganhos.txt"));
		        System.out.println("\nGastos: \n");
		        System.out.println(lista("gastos.txt"));
				break;


	        case "estatisticas":
				String[] informacoesGastos = estatisticas("gastos.txt");
				String[] informacoesGanhos = estatisticas("ganhos.txt");
				String[] informacoesTotais = new String[3];
				informacoesTotais[0] = String.valueOf((Float.parseFloat(informacoesGastos[0]) + Float.parseFloat(informacoesGanhos[0])));
		        informacoesTotais[1] = String.valueOf((Integer.parseInt(informacoesGastos[1]) + Integer.parseInt(informacoesGanhos[1])));
				informacoesTotais[2] = String.valueOf((Float.parseFloat(informacoesTotais[0])/Integer.parseInt(informacoesTotais[1])));
		        System.out.println("\n==============================" +
				        "\n   Estatísticas:" +
				        "\nLucro total: " + String.format("%.2f", Float.parseFloat(informacoesTotais[0].replace(",","."))) +
				        "\nGanhos totais: " + String.format("%.2f", Float.parseFloat(informacoesGanhos[0].replace(",","."))) +
				        "\nGastos totais: " + String.format("%.2f", Float.parseFloat(informacoesGastos[0].replace(",","."))) +
				        "\n\nMédia total: " + String.format("%.2f", Float.parseFloat(informacoesTotais[2].replace(",","."))) +
				        "\nMédia de ganhos: " + String.format("%.2f", Float.parseFloat(informacoesGanhos[2].replace(",","."))) +
				        "\nMédia de gastos: " + String.format("%.2f", Float.parseFloat(informacoesGastos[2].replace(",","."))) +
				        "\n\nTotal de registros: " + Integer.parseInt(informacoesTotais[1]) +
				        "\nRegistros de ganhos: " + Integer.parseInt(informacoesGanhos[1]) +
				        "\nRegistros de gastos: " + Integer.parseInt(informacoesGastos[1]) +
				        "\n==============================\n");
				break;



	        case "remover":
		        System.out.println("Qual o ID do registro que deseja remover? Para sair, use 'cancelar'.");
		        String entradaRemover = System.console().readLine();
		        try {
			        if (Objects.equals(entradaRemover, "cancelar")) {
				        break;
			        }
					else if (Integer.parseInt(entradaRemover) > 89999) {
				        removerID("gastos.txt", entradaRemover);
						removerID("ganhos.txt", entradaRemover);
			        } else {
				        System.out.println("ID em formato inválido!");
			        }
		        } catch (NumberFormatException e) {
			        System.out.println("Comando inválido!");
		        }
				break;



	        case "adicionar":
		        System.out.println("Qual foi o gasto ou ganho? Para gasto, use " +
				        "números negativos. Para sair, use 'cancelar'.");
		        String entradaAdicionar = System.console().readLine();
				try {
			        if (Objects.equals(entradaAdicionar, "cancelar")) {
				        break;
			        } else if (Float.parseFloat(entradaAdicionar) >= 0) {
						ID = aleatorizarID();
						String caminhoDoArquivo = "ganhos.txt";
				        System.out.println("Deseja adicionar descrição? Deixe vazio para que nada apareça.");
						String entradaDescricao = System.console().readLine();
				        DateTimeFormatter formatacaoTempo = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				        LocalDateTime tempoAtualSemFormatacao = LocalDateTime.now();
				        String tempoAtual = (formatacaoTempo.format(tempoAtualSemFormatacao));
				        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoDoArquivo, true))) {
					        writer.write("\n\nID:" + ID +
							        "\n==============================" +
							        "\nGanhos: " + String.format("%.2f", (Float.parseFloat(entradaAdicionar))) +
							        "\nData: " + tempoAtual +
							        "\nDescrição: " + entradaDescricao +
							        "\n==============================");
				        }
					} else if (Float.parseFloat(entradaAdicionar) < 0) {
				        ID = aleatorizarID();
						String caminhoDoArquivo = "gastos.txt";
				        System.out.println("Deseja adicionar descrição? Deixe vazio para que nada apareça.");
				        String entradaDescricao = System.console().readLine();
				        DateTimeFormatter formatacaoTempo = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				        LocalDateTime tempoAtualSemFormatacao = LocalDateTime.now();
				        String tempoAtual = (formatacaoTempo.format(tempoAtualSemFormatacao));
				        try (BufferedWriter writer = new BufferedWriter(new FileWriter(caminhoDoArquivo, true)))
				        {
					        writer.write("\n\nID:" + ID +
							        "\n==============================" +
							        "\nGastos: " + String.format("%.2f", (Float.parseFloat(entradaAdicionar))) +
							        "\nData: " + tempoAtual +
							        "\nDescrição: " + entradaDescricao +
							        "\n==============================");
				        }
					}
				} catch (NumberFormatException e) {
					System.out.println("Comando inválido!");
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				break;



	        case "ajuda":
                System.out.print("""
		    
						Os seguintes comandos são válidos:
						ajuda -> Mostra este menu de comandos.
						adicionar -> Adiciona um novo registro à lista.
						remover -> Dado um ID, remove um item da lista.
						estatisticas -> Mostra uma lista de estatísticas úteis.
						lista -> Envia todo o conteúdo de gastos e ganhos.
						sair -> Sai do programa.
		    
						""");
                        break;
            default:
                System.out.println("Comando inválido! Digite 'ajuda' para ver todos os comandos.");
        }
    }


    public static void main(String[] args) throws IOException {
        while (funcionando) {
	        System.out.print("Digite a operação: ");
	        String entrada = (System.console().readLine()).toLowerCase();
            switchCaseOperacao(entrada);
        }
    }
}
