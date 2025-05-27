import { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { type Item, itemService } from '../../services/itemService';
import ItemForm from '../../components/forms/ItemForm';
import './ItemManagement.css';

export default function ItemManagement() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const location = useLocation();
  const [item, setItem] = useState<Item | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  
  // Determinar se é novo baseado na rota
  const isNew = location.pathname.includes('/new') || id === 'new';

  useEffect(() => {
    if (!isNew && id && id !== 'new') {
      fetchItem();
    }
  }, [id, isNew]);

  const fetchItem = async () => {
    if (!id || id === 'new' || isNew) {
      return;
    }
    
    try {
      setLoading(true);
      const data = await itemService.getById(id);
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
    console.log('handleSubmit called with:', { id, isNew, data, pathname: location.pathname });
    
    try {
      setLoading(true);
      if (isNew) {
        console.log('Creating new service...');
        await itemService.create(data);
        setSuccess('Serviço criado com sucesso!');
      } else if (id && id !== 'new') {
        console.log('Updating existing service with id:', id);
        await itemService.update(id, data);
        setSuccess('Serviço atualizado com sucesso!');
      } else {
        console.log('Invalid state:', { id, isNew, pathname: location.pathname });
        throw new Error('Estado inválido para salvar serviço');
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