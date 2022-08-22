package Main;

import Utils.Inputs;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


public class Pedido {

    private static ArrayList<Item> listaDeItens = new ArrayList();
    protected static double valorTotalDoPedido = 0;
    protected static Map<Double, Integer> qtd = new LinkedHashMap<>();
	private static double troco;
	public static double pagamento;
	public static int[] tipoNota = {10000, 5000, 2000, 1000, 500, 200, 100, 50, 25, 10, 5, 1};

    public static void calculaValorTotal() {
        double subTotal = 0;
        for (Item item : listaDeItens) {
            subTotal += item.getValorDoItem();
        }
        valorTotalDoPedido = subTotal;
    }
    
    public static boolean isPagamento() {
		 while (pagamento < Pedido.valorTotalDoPedido) {
			return false;
		}
		return true;
		
	}
    
    public static double getTroco() {
		pagamento = valorPagar();
		double d;
		while (isPagamento() != true) {
			System.out.println("Pagamento insuficiente para compra");
			pagamento = valorPagar();
		}
		d = pagamento - Pedido.valorTotalDoPedido;
		
		troco = (double) (Math.round(d *100.0)/100.0);
		return troco;
	}
    
    public static void setTroco(double troco) {
		Pedido.troco = troco;
	}
    
    private static void notasEntregue() {
		int centavo = (int) (troco * 100);	
		for(int nota : tipoNota) {
			if (centavo >= nota) {
				int total = centavo / nota;
				qtd.put(nota / 100.0, total);
				centavo %= nota;
				if (centavo == 0) {
					break;
				}
			}
		}
	}

    public static boolean adicionaItemNaLista(Produto produto, int quantidade) {
        for (Item item : listaDeItens) {
            if (item.getProduto().getNome().equalsIgnoreCase(produto.getNome())) {
                Estoque.darBaixaEmEstoque(item.getProduto().getId(), quantidade);
                item.setQuantidade(item.getQuantidade() + quantidade);
                item.defineValorTotal();
                System.out.println("Foi adicionada a quantidade ao item já existente.");
                return false;
            }
        }
        listaDeItens.add(new Item(produto, quantidade));
        Estoque.darBaixaEmEstoque(produto.getId(), quantidade);
        System.out.println("Foi adicionado o produto na lista de compras.");
        return true;
    }

    public static void imprimePedido() {
        System.out.println("                              NOTA FISCAL");
        System.out.printf("ID       |NOME            |PRECO UN           |QUANTIDADE   |PRECO ITEM \n");
        for (Item item : listaDeItens) {
            System.out.printf("%-8d | %-14s | R$%-15.2f | %-10d  | R$%.2f\n"
                    , item.getProduto().getId(), item.getProduto().getNome(), item.getProduto().getPreco(), item.getQuantidade(), item.getValorDoItem());
        }
        imprimeValorTotal();
    }

    private static void imprimeValorTotal() {
        System.out.println();
        System.out.printf("Total: R$%.2f", valorTotalDoPedido);
        System.out.println("________________________________________________________________________");
        System.out.println();
        System.out.println();
    }

    public static void adicionaItem(){
        String nome = recebeNomeDoTeclado();
        int quantidade = recebeQuantidadeDoTeclado();
        Produto produto = Estoque.encontraProduto(nome);
        if (quantidade > Estoque.getQuantidadeAtualEmEstoque(produto)) {
        	System.out.println("Quantidade solicitada em falta no Estoque.");
        }else if(produto != null){
            adicionaItemNaLista(produto,quantidade);
            calculaValorTotal();
        } else {
            System.out.println("Produto nao encontrado");
        }

    }
    
    public static void imprimeFimDoPedido() {
		getTroco();
        Pedido.imprimePedido();
        ImprimeValorPago();
        imprimeTroco();
    }

	private static void ImprimeValorPago() {
    	System.out.println();
    	System.out.printf("Valor pago: R$%.2f", pagamento);
    	System.out.println("___________________________________________________________________");

    }
	
	private static void imprimeTroco() {
    	System.out.println();
    	System.out.printf("Troco: R$%.2f", troco);
    	System.out.println("_________________________________________________________________________");
    	imprimeNotas();
        System.out.println();
        System.out.println();
    }
	
	private static void imprimeNotas() {
		 notasEntregue();
		 System.out.print("|| ");
		for (Map.Entry<Double, Integer> ps : qtd.entrySet()) {
			double num = ps.getKey();
			int quant = ps.getValue();
			String tipo;
			if (num > 1) {
				tipo ="cedula";
			}else {
				tipo = "moeda";
			}
			System.out.printf("%d %s%s de %.2f || ", quant, tipo, quant > 1 ? "s" : "", num );
		}
	}

    public static String recebeNomeDoTeclado(){
        System.out.print("Digite o nome: ");
        return Inputs.inputString();
    }


    public static int recebeQuantidadeDoTeclado(){
        System.out.print("Digite a quantidade: ");
        return Inputs.inputInt();
    }
    
    public static double valorPagar(){
        System.out.print("Faça o pagamento:");
        return Inputs.inputDouble();
    }

    public void limparCarrinho() {
        listaDeItens.clear();
    }

    public static ArrayList<Item> getListaDeItens() {
        return listaDeItens;
    }

    public void setListaDeItens(ArrayList<Item> listaDeItens) {
        Pedido.listaDeItens = listaDeItens;
    }

    public double getValorTotalDoPedido() {
        return valorTotalDoPedido;
    }

    public void setValorTotalDoPedido(double valorTotalDoPedido) {
        Pedido.valorTotalDoPedido = valorTotalDoPedido;
    }
}
