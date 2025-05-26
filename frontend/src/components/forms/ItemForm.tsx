import { useState, useEffect } from 'react';
import { type Item, itemService } from '../../services/itemService';
import { type Company, companyService } from '../../services/companyService';
import './CompanyForm.css';

// Função para formatar valor para moeda brasileira
const formatCurrency = (value: number | string): string => {
  const numValue = typeof value === 'string' ? parseFloat(value) : value;
  return numValue.toLocaleString('pt-BR', {
    style: 'currency',
    currency: 'BRL',
  });
};

// Função para converter string de moeda para número
const parseCurrency = (value: string): number => {
  return parseFloat(value.replace(/[^\d,.-]/g, '').replace(',', '.'));
};

interface ItemFormProps {
  initialData?: Item;
  onSubmit: (data: Item) => void;
  onCancel: () => void;
}

const emptyItem: Item = {
  name: '',
  description: '',
  price: 0,
  durationMinutes: 30,
  requiresPrePayment: false,
  companyId: '',
  active: true
};

export default function ItemForm({ initialData = emptyItem, onSubmit, onCancel }: ItemFormProps) {
  const [formData, setFormData] = useState<Item>(initialData);
  const [companies, setCompanies] = useState<Company[]>([]);
  const [professionals, setProfessionals] = useState<any[]>([]);
  const [assignedProfessionals, setAssignedProfessionals] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('service-info');
  const [formattedPrice, setFormattedPrice] = useState(formatCurrency(initialData.price || 0));
  
  // Para informações de erro de validação
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    const fetchCompanies = async () => {
      try {
        const data = await companyService.getAll();
        setCompanies(data);
        // Se não tiver uma empresa selecionada e houver empresas disponíveis
        if (!formData.companyId && data.length > 0) {
          setFormData(prev => ({
            ...prev,
            companyId: data[0].id || ''
          }));
        }
      } catch (err) {
        setError('Erro ao carregar empresas');
        console.error('Erro:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchCompanies();
  }, []);

  // Carregar profissionais quando já existe um ID de serviço
  useEffect(() => {
    if (initialData.id) {
      fetchProfessionalsByService(initialData.id);
    }
  }, [initialData.id]);

  // Função para buscar profissionais associados ao serviço
  const fetchProfessionalsByService = async (serviceId: string) => {
    try {
      // TODO: Implementar endpoint no backend para buscar profissionais por serviço
      // const data = await professionalService.getByService(serviceId);
      // setProfessionals(data);
      // setAssignedProfessionals(data.map(p => p.id));
    } catch (err) {
      setError('Erro ao carregar profissionais');
      console.error('Erro:', err);
    }
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};
    
    // Validar campos obrigatórios do serviço
    if (!formData.name) newErrors.name = "O nome do serviço é obrigatório";
    if (!formData.price || formData.price <= 0) newErrors.price = "O preço deve ser maior que zero";
    if (!formData.durationMinutes || formData.durationMinutes <= 0) newErrors.durationMinutes = "A duração deve ser maior que zero";
    if (!formData.companyId) newErrors.companyId = "A empresa é obrigatória";
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value, type } = e.target as HTMLInputElement;
    
    if (name === 'price') {
      // Atualiza o valor formatado exibido no input
      setFormattedPrice(formatCurrency(parseCurrency(value)));
      // Atualiza o valor numérico no estado
      setFormData({
        ...formData,
        price: parseCurrency(value)
      });
    } else if (type === 'checkbox') {
      const { checked } = e.target as HTMLInputElement;
      setFormData({
        ...formData,
        [name]: checked
      });
    } else {
      setFormData({
        ...formData,
        [name]: value
      });
    }
    
    // Limpar erro após edição
    if (errors[name]) {
      setErrors({
        ...errors,
        [name]: ''
      });
    }
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (validateForm()) {
      onSubmit(formData);
    }
  };

  const handleToggleProfessional = (professionalId: string) => {
    setAssignedProfessionals(prev => {
      if (prev.includes(professionalId)) {
        return prev.filter(id => id !== professionalId);
      } else {
        return [...prev, professionalId];
      }
    });
  };

  if (loading) return <div>Carregando empresas...</div>;
  if (error) return <div className="error-message">{error}</div>;
  if (companies.length === 0) return <div>Nenhuma empresa cadastrada. Por favor, cadastre uma empresa primeiro.</div>;

  return (
    <form onSubmit={handleSubmit} className="company-form">
      <h2 className="form-section-header">{initialData.id ? 'Editar Serviço' : 'Novo Serviço'}</h2>
      
      <div className="tabs">
        <button 
          type="button"
          className={`tab-button ${activeTab === 'service-info' ? 'active' : ''}`}
          onClick={() => setActiveTab('service-info')}
        >
          Informações do Serviço
        </button>
        <button 
          type="button"
          className={`tab-button ${activeTab === 'professionals' ? 'active' : ''}`}
          onClick={() => setActiveTab('professionals')}
          disabled={!initialData.id}
        >
          Profissionais
        </button>
      </div>
      
      {activeTab === 'service-info' && (
        <div className="tab-content">
          <div className="form-group full-width">
            <label>Empresa*</label>
            <select
              name="companyId"
              value={formData.companyId}
              onChange={handleChange}
              required
              className={errors.companyId ? 'error' : ''}
            >
              <option value="">Selecione uma empresa</option>
              {companies.map(company => (
                <option key={company.id} value={company.id}>
                  {company.name}
                </option>
              ))}
            </select>
            {errors.companyId && <div className="error-message">{errors.companyId}</div>}
          </div>

          <div className="form-group full-width">
            <label>Nome do Serviço*</label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
              className={errors.name ? 'error' : ''}
              placeholder="Nome do serviço"
            />
            {errors.name && <div className="error-message">{errors.name}</div>}
          </div>

          <div className="form-group full-width">
            <label>Descrição</label>
            <textarea
              name="description"
              value={formData.description || ''}
              onChange={handleChange}
              placeholder="Descrição detalhada do serviço"
              rows={4}
            />
          </div>

          <div className="form-group">
            <label>Preço*</label>
            <input
              type="text"
              name="price"
              value={formattedPrice}
              onChange={handleChange}
              required
              className={errors.price ? 'error' : ''}
              placeholder="R$ 0,00"
            />
            {errors.price && <div className="error-message">{errors.price}</div>}
          </div>

          <div className="form-group">
            <label>Duração (minutos)*</label>
            <input
              type="number"
              name="durationMinutes"
              value={formData.durationMinutes}
              onChange={handleChange}
              required
              min="1"
              className={errors.durationMinutes ? 'error' : ''}
              placeholder="Duração em minutos"
            />
            {errors.durationMinutes && <div className="error-message">{errors.durationMinutes}</div>}
          </div>

          <div className="form-group checkbox-group">
            <input
              type="checkbox"
              id="requiresPrePayment"
              name="requiresPrePayment"
              checked={formData.requiresPrePayment}
              onChange={handleChange}
            />
            <label htmlFor="requiresPrePayment">Requer pagamento antecipado</label>
          </div>

          <div className="form-group checkbox-group">
            <input
              type="checkbox"
              id="active"
              name="active"
              checked={formData.active !== false}
              onChange={handleChange}
            />
            <label htmlFor="active">Ativo</label>
          </div>
        </div>
      )}
      
      {activeTab === 'professionals' && (
        <div className="tab-content">
          {!initialData.id ? (
            <div className="no-subsidiaries-message">
              Salve primeiro o serviço para gerenciar profissionais.
            </div>
          ) : (
            <div className="subsidiaries-panel">
              <div className="panel-header">
                <h3>Profissionais que realizam este serviço</h3>
              </div>
              
              {professionals.length === 0 ? (
                <div className="no-data">
                  Nenhum profissional encontrado. Adicione profissionais na aba de profissionais da subsidiária.
                </div>
              ) : (
                <div className="professionals-list">
                  {professionals.map(professional => (
                    <div key={professional.id} className="professional-item">
                      <input
                        type="checkbox"
                        id={`prof-${professional.id}`}
                        checked={assignedProfessionals.includes(professional.id)}
                        onChange={() => handleToggleProfessional(professional.id)}
                      />
                      <label htmlFor={`prof-${professional.id}`}>
                        {professional.name}
                      </label>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      )}

      <div className="form-buttons">
        <button type="button" className="secondary" onClick={onCancel}>Cancelar</button>
        <button type="submit" className="primary">Salvar</button>
      </div>
    </form>
  );
}