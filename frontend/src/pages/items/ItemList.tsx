import { useState, useEffect } from 'react';
import { type Item, itemService } from '../../services/itemService';
import { useNavigate } from 'react-router-dom';

export default function ItemList() {
  const [items, setItems] = useState<Item[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchItems();
  }, []);

  const fetchItems = async () => {
    try {
      setLoading(true);
      const data = await itemService.getAll();
      setItems(data);
      setError('');
    } catch (err: any) {
      setError(`Erro ao carregar serviços: ${err.message}`);
      console.error('Erro:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (id: string) => {
    navigate(`/services/${id}/edit`);
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm('Tem certeza que deseja excluir este serviço?')) {
      return;
    }

    try {
      await itemService.delete(id);
      fetchItems();
    } catch (err: any) {
      setError(`Erro ao excluir serviço: ${err.message}`);
      console.error('Erro:', err);
    }
  };

  const handleAdd = () => {
    navigate('/services/new');
  };

  // Função para formatar preço
  const formatPrice = (price: number) => {
    return price.toLocaleString('pt-BR', {
      style: 'currency',
      currency: 'BRL',
    });
  };

  if (loading) return <div className="loading">Carregando serviços...</div>;

  return (
    <div className="item-list-container">
      <div className="list-header">
        <h2>Serviços</h2>
        <button className="add-button" onClick={handleAdd}>+ Novo Serviço</button>
      </div>

      {error && <div className="error-message">{error}</div>}

      {items.length === 0 ? (
        <div className="no-data">
          Nenhum serviço cadastrado. Clique em "Novo Serviço" para adicionar.
        </div>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>Nome</th>
              <th>Preço</th>
              <th>Duração</th>
              <th>Pgto. Antecipado</th>
              <th>Status</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {items.map(item => (
              <tr key={item.id}>
                <td>{item.name}</td>
                <td>{formatPrice(item.price)}</td>
                <td>{item.durationMinutes} min</td>
                <td>{item.requiresPrePayment ? 'Sim' : 'Não'}</td>
                <td>{item.active !== false ? 'Ativo' : 'Inativo'}</td>
                <td className="action-buttons">
                  <button 
                    className="edit-button"
                    onClick={() => handleEdit(item.id!)}
                  >
                    Editar
                  </button>
                  <button 
                    className="delete-button"
                    onClick={() => handleDelete(item.id!)}
                  >
                    Excluir
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}