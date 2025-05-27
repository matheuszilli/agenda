import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { type Item, itemService } from '../../services/itemService';
import { type Company, companyService } from '../../services/companyService';
import { type Subsidiary, subsidiaryService } from '../../services/subsidiaryService';
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
  subsidiaryId: '',
  active: true
};

export default function ItemForm({ initialData = emptyItem, onSubmit, onCancel }: ItemFormProps) {
  const [searchParams] = useSearchParams();
  const subsidiaryIdFromUrl = searchParams.get('subsidiaryId');
  
  const [formData, setFormData] = useState<Item>({
    ...initialData,
    subsidiaryId: initialData.subsidiaryId || subsidiaryIdFromUrl || ''
  });
  const [companies, setCompanies] = useState<Company[]>([]);
  const [subsidiaries, setSubsidiaries] = useState<Subsidiary[]>([]);
  const [professionals, setProfessionals] = useState<any[]>([]);
  const [assignedProfessionals, setAssignedProfessionals] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('service-info');
  const [formattedPrice, setFormattedPrice] = useState(
    initialData.price && initialData.price > 0 ? formatCurrency(initialData.price) : ''
  );
  
  // Para informações de erro de validação
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    const fetchData = async () => {
      try {
        const companiesData = await companyService.getAll();
        setCompanies(companiesData);
        
        // Se não tiver uma empresa selecionada e houver empresas disponíveis
        if (!formData.companyId && companiesData.length > 0) {
          setFormData(prev => ({
            ...prev,
            companyId: companiesData[0].id || ''
          }));
        }
      } catch (err) {
        setError('Erro ao carregar empresas');
        console.error('Erro:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  // Carregar subsidiárias quando a empresa mudar
  useEffect(() => {
    if (formData.companyId) {
      fetchSubsidiaries(formData.companyId);
    }
  }, [formData.companyId]);

  const fetchSubsidiaries = async (companyId: string) => {
    try {
      const data = await subsidiaryService.getByCompany(companyId);
      setSubsidiaries(data);
      
      // Se veio um subsidiaryId da URL e ele pertence à empresa, mantém
      // Senão, seleciona a primeira subsidiária disponível
      if (subsidiaryIdFromUrl && data.some(s => s.id === subsidiaryIdFromUrl)) {
        setFormData(prev => ({ ...prev, subsidiaryId: subsidiaryIdFromUrl }));
      } else if (!formData.subsidiaryId && data.length > 0) {
        setFormData(prev => ({ ...prev, subsidiaryId: data[0].id || '' }));
      }
    } catch (err) {
      console.error('Erro ao carregar subsidiárias:', err);
    }
  };

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
    if (!formData.subsidiaryId) newErrors.subsidiaryId = "A subsidiária é obrigatória";
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value, type } = e.target as HTMLInputElement;
    
    if (name === 'price') {
      // Permitir apenas números, vírgula e ponto
      const numericValue = value.replace(/[^\d,.-]/g, '');
      // Converter vírgula para ponto para cálculos
      const normalizedValue = numericValue.replace(',', '.');
      const parsedValue = parseFloat(normalizedValue) || 0;
      
      // Atualizar o valor numérico no estado
      setFormData({
        ...formData,
        price: parsedValue
      });
      
      // Manter o valor digitado no input (sem formatação automática)
      setFormattedPrice(numericValue);
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

  const handlePriceBlur = () => {
    // Formatar o preço quando o usuário sair do campo
    if (formData.price > 0) {
      setFormattedPrice(formatCurrency(formData.price));
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
            <label>Subsidiária*</label>
            <select
              name="subsidiaryId"
              value={formData.subsidiaryId}
              onChange={handleChange}
              required
              className={errors.subsidiaryId ? 'error' : ''}
              disabled={!formData.companyId}
            >
              <option value="">Selecione uma subsidiária</option>
              {subsidiaries.map(subsidiary => (
                <option key={subsidiary.id} value={subsidiary.id}>
                  {subsidiary.name}
                </option>
              ))}
            </select>
            {errors.subsidiaryId && <div className="error-message">{errors.subsidiaryId}</div>}
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
              placeholder="Ex: 50,00"
              onBlur={handlePriceBlur}
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