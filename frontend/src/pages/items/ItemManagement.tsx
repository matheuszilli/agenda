import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { type Item, itemService } from '../../services/itemService';
import ItemForm from '../../components/forms/ItemForm';
import './ItemManagement.css';

export default function ItemManagement() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [item, setItem] = useState<Item | null>(null);
  const [loading, setLoading] = useState(id !== 'new');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const isNew = id === 'new';

  useEffect(() => {
    if (!isNew) {
      fetchItem();
    }
  }, [id]);

  const fetchItem = async () => {
    try {
      setLoading(true);
      const data = await itemService.getById(id!);
      setItem(data);
      setError('');
    } catch (err: any) {
      setError(`Erro ao carregar serviço: ${err.message}`);
      console.error('Erro:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (data: Item) => {
    try {
      setLoading(true);
      if (isNew) {
        await itemService.create(data);
        setSuccess('Serviço criado com sucesso!');
      } else {
        await itemService.update(id!, data);
        setSuccess('Serviço atualizado com sucesso!');
      }
      
      // Redirecionar para a lista após um breve delay
      setTimeout(() => {
        navigate('/services');
      }, 1500);
      
    } catch (err: any) {
      setError(`Erro ao salvar serviço: ${err.message}`);
      console.error('Erro:', err);
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate('/services');
  };

  if (loading) return <div className="loading">Carregando...</div>;
  if (!isNew && error) return <div className="error-message">{error}</div>;
  if (success) return <div className="success-message">{success}</div>;

  return (
    <div className="item-management-container">
      <ItemForm 
        initialData={item || undefined}
        onSubmit={handleSubmit}
        onCancel={handleCancel}
      />
    </div>
  );
}