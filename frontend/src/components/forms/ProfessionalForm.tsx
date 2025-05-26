import { useState, useEffect } from 'react';
import { type Professional, professionalService, type ProfessionalServiceConfig } from '../../services/professionalService';
import { type Item, itemService } from '../../services/itemService';
import { subsidiaryService } from '../../services/subsidiaryService';
import './CompanyForm.css';
import './ItemForm.css';

// Máscara de formatação para CPF
const formatCPF = (value: string) => {
  if (!value) return '';
  // Remove tudo que não é dígito
  const digits = value.replace(/\D/g, '');
  
  // Aplica máscara XXX.XXX.XXX-XX
  return digits
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1-$2')
    .substring(0, 14);
};

// Máscara de formatação para telefone
const formatPhone = (value: string) => {
  if (!value) return '';
  // Remove tudo que não é dígito
  const digits = value.replace(/\D/g, '');
  
  // Aplica máscara (XX) XXXXX-XXXX
  if (digits.length <= 10) {
    return digits
      .replace(/(\d{2})(\d)/, '($1) $2')
      .replace(/(\d{4})(\d)/, '$1-$2')
      .substring(0, 14);
  } else {
    return digits
      .replace(/(\d{2})(\d)/, '($1) $2')
      .replace(/(\d{5})(\d)/, '$1-$2')
      .substring(0, 15);
  }
};

// Máscara de formatação para CEP
const formatCEP = (value: string) => {
  if (!value) return '';
  // Remove tudo que não é dígito
  const digits = value.replace(/\D/g, '');
  
  // Aplica máscara XXXXX-XXX
  return digits
    .replace(/(\d{5})(\d)/, '$1-$2')
    .substring(0, 9);
};

interface ProfessionalFormProps {
  initialData?: Professional;
  onSubmit: (data: Professional) => void;
  onCancel: () => void;
}

const emptyProfessional: Professional = {
  firstName: '',
  lastName: '',
  documentNumber: '',
  address: {
    street: '',
    number: '',
    complement: '',
    neighbourhood: '',
    city: '',
    state: '',
    zipCode: ''
},
  phone: '',
  email: '',
  subsidiaryId: '',
  services: []
};

export default function ProfessionalForm({ initialData = emptyProfessional, onSubmit, onCancel }: ProfessionalFormProps) {
  const [formData, setFormData] = useState<Professional>(initialData);
  const [subsidiaries, setSubsidiaries] = useState<any[]>([]);
  const [services, setServices] = useState<Item[]>([]);
  const [selectedServices, setSelectedServices] = useState<string[]>([]);
  const [serviceConfigs, setServiceConfigs] = useState<ProfessionalServiceConfig[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('professional-info');
  
  // Para informações de erro de validação
  const [errors, setErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    const fetchSubsidiaries = async () => {
      try {
        const data = await subsidiaryService.getAll();
        setSubsidiaries(data);
        // Se não tiver uma subsidiária selecionada e houver subsidiárias disponíveis
        if (!formData.subsidiaryId && data.length > 0) {
          setFormData(prev => ({
            ...prev,
            subsidiaryId: data[0].id || ''
          }));
        }
      } catch (err) {
        setError('Erro ao carregar subsidiárias');
        console.error('Erro:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchSubsidiaries();
  }, []);

  // Quando a subsidiária mudar, buscar serviços disponíveis
  useEffect(() => {
    if (formData.subsidiaryId) {
      fetchServicesBySubsidiary(formData.subsidiaryId);
    }
  }, [formData.subsidiaryId]);

  // Carregar serviços associados quando já existe um ID de profissional
  useEffect(() => {
    if (initialData.id) {
      fetchProfessionalServices(initialData.id);
    }
  }, [initialData.id]);

  const fetchServicesBySubsidiary = async (subsidiaryId: string) => {
    try {
      // Ajustar para o endpoint correto quando disponível
      const data = await itemService.getBySubsidiary(subsidiaryId);
      setServices(data);
    } catch (err) {
      console.error('Erro ao carregar serviços:', err);
    }
  };

  const fetchProfessionalServices = async (professionalId: string) => {
    try {
      const data = await professionalService.getServices(professionalId);
      setServiceConfigs(data);
      setSelectedServices(data.map(config => config.serviceId));
    } catch (err) {
      console.error('Erro ao carregar serviços do profissional:', err);
    }
  };

  const validateForm = () => {
    const newErrors: Record<string, string> = {};
    
    // Validar campos obrigatórios do profissional
    if (!formData.firstName) newErrors.firstName = "O nome é obrigatório";
    if (!formData.lastName) newErrors.lastName = "O sobrenome é obrigatório";
    if (!formData.documentNumber) newErrors.documentNumber = "O CPF é obrigatório";
    if (!formData.email) newErrors.email = "O email é obrigatório";
    if (!formData.phone) newErrors.phone = "O telefone é obrigatório";
    if (!formData.subsidiaryId) newErrors.subsidiaryId = "A subsidiária é obrigatória";
    
    // Validar campos obrigatórios do endereço
    if (!formData.address.street) newErrors['address.street'] = "A rua é obrigatória";
    if (!formData.address.number) newErrors['address.number'] = "O número é obrigatório";
    if (!formData.address.city) newErrors['address.city'] = "A cidade é obrigatória";
    if (!formData.address.state) newErrors['address.state'] = "O estado é obrigatório";
    if (!formData.address.zipCode) newErrors['address.zipCode'] = "O CEP é obrigatório";
    
    // Validação de formato do email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (formData.email && !emailRegex.test(formData.email)) {
      newErrors.email = "Formato de email inválido";
    }
    
    // Validações de formato do CPF
    const cpfDigits = formData.documentNumber.replace(/\D/g, '');
    if (cpfDigits.length > 0 && cpfDigits.length !== 11) {
      newErrors.documentNumber = "CPF inválido. Deve ter 11 dígitos";
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const {name, value} = e.target;

    // Aplicar máscaras de formatação quando necessário
    let formattedValue = value;
    if (name === 'documentNumber') {
      formattedValue = formatCPF(value);
    } else if (name === 'phone') {
      formattedValue = formatPhone(value);
    } else if (name === 'address.zipCode') {
      formattedValue = formatCEP(value);
    }

    if (name.includes('.')) {
      const [parent, child] = name.split('.');
      setFormData({
        ...formData,
        [parent]: {
          ...formData[parent as keyof Professional] as Record<string, any>,
          [child]: formattedValue
        }
      });
    } else {
      setFormData({
        ...formData,
        [name]: formattedValue
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

  const handleServiceToggle = (serviceId: string) => {
    setSelectedServices(prev => {
      if (prev.includes(serviceId)) {
        // Remover serviço
        return prev.filter(id => id !== serviceId);
      } else {
        // Adicionar serviço
        return [...prev, serviceId];
      }
    });

    // Atualizar configurações de serviço
    if (!selectedServices.includes(serviceId)) {
      // Adicionar nova configuração quando selecionado
      setServiceConfigs(prev => [
        ...prev,
        {
          serviceId,
          commissionType: 'PERCENTAGE',
          commissionValue: 0
        }
      ]);
    } else {
      // Remover configuração quando desmarcado
      setServiceConfigs(prev => 
        prev.filter(config => config.serviceId !== serviceId)
      );
    }
  };

  const handleServiceConfigChange = (serviceId: string, field: keyof ProfessionalServiceConfig, value: string | number) => {
    setServiceConfigs(prev => {
      return prev.map(config => {
        if (config.serviceId === serviceId) {
          return { ...config, [field]: value };
        }
        return config;
      });
    });
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (validateForm()) {
      // Incluir as configurações de serviço no formulário
      const professionalWithServices = {
        ...formData,
        services: serviceConfigs
      };
      onSubmit(professionalWithServices);
    }
  };

  if (loading) return <div>Carregando subsidiárias...</div>;
  if (error) return <div className="error-message">{error}</div>;
  if (subsidiaries.length === 0) return <div>Nenhuma subsidiária cadastrada. Por favor, cadastre uma subsidiária primeiro.</div>;

  return (
    <form onSubmit={handleSubmit} className="company-form">
      <h2 className="form-section-header">{initialData.id ? 'Editar Profissional' : 'Novo Profissional'}</h2>
      
      <div className="tabs">
        <button 
          type="button"
          className={`tab-button ${activeTab === 'professional-info' ? 'active' : ''}`}
          onClick={() => setActiveTab('professional-info')}
        >
          Informações do Profissional
        </button>
        <button 
          type="button"
          className={`tab-button ${activeTab === 'services' ? 'active' : ''}`}
          onClick={() => setActiveTab('services')}
        >
          Serviços
        </button>
        <button 
          type="button"
          className={`tab-button ${activeTab === 'schedule' ? 'active' : ''}`}
          onClick={() => setActiveTab('schedule')}
          disabled={!initialData.id}
        >
          Agenda
        </button>
      </div>
      
      {activeTab === 'professional-info' && (
        <div className="tab-content">
          <div className="form-group">
            <label>Subsidiária*</label>
            <select
              name="subsidiaryId"
              value={formData.subsidiaryId}
              onChange={handleChange}
              required
              className={errors.subsidiaryId ? 'error' : ''}
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

          <div className="form-group">
            <label>Nome*</label>
            <input
              type="text"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              required
              className={errors.firstName ? 'error' : ''}
              placeholder="Nome"
            />
            {errors.firstName && <div className="error-message">{errors.firstName}</div>}
          </div>

          <div className="form-group">
            <label>Sobrenome*</label>
            <input
              type="text"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              required
              className={errors.lastName ? 'error' : ''}
              placeholder="Sobrenome"
            />
            {errors.lastName && <div className="error-message">{errors.lastName}</div>}
          </div>

          <div className="form-group">
            <label>CPF*</label>
            <input
              type="text"
              name="documentNumber"
              value={formData.documentNumber}
              onChange={handleChange}
              required
              placeholder="XXX.XXX.XXX-XX"
              className={errors.documentNumber ? 'error' : ''}
              maxLength={14}
            />
            {errors.documentNumber && <div className="error-message">{errors.documentNumber}</div>}
          </div>

          <div className="form-group">
            <label>Email*</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              required
              className={errors.email ? 'error' : ''}
              placeholder="email@exemplo.com"
            />
            {errors.email && <div className="error-message">{errors.email}</div>}
          </div>

          <div className="form-group">
            <label>Telefone*</label>
            <input
              type="text"
              name="phone"
              value={formData.phone}
              onChange={handleChange}
              required
              placeholder="(XX) XXXXX-XXXX"
              className={errors.phone ? 'error' : ''}
              maxLength={15}
            />
            {errors.phone && <div className="error-message">{errors.phone}</div>}
          </div>

          <h3 className="form-section-header">Endereço</h3>
          <br />

          <div className="form-group">
            <label>Rua*</label>
            <input
              type="text"
              name="address.street"
              value={formData.address.street}
              onChange={handleChange}
              required
              placeholder="Av., Rua, etc."
              className={errors['address.street'] ? 'error' : ''}
            />
            {errors['address.street'] && <div className="error-message">{errors['address.street']}</div>}
          </div>

          <div className="form-group">
            <label>Número*</label>
            <input
              type="text"
              name="address.number"
              value={formData.address.number}
              onChange={handleChange}
              required
              placeholder="Número"
              className={errors['address.number'] ? 'error' : ''}
            />
            {errors['address.number'] && <div className="error-message">{errors['address.number']}</div>}
          </div>

          <div className="form-group">
            <label>Complemento</label>
            <input
              type="text"
              name="address.complement"
              value={formData.address.complement || ''}
              onChange={handleChange}
              placeholder="Sala, Andar, etc."
            />
          </div>

          <div className="form-group">
            <label>Bairro</label>
            <input
              type="text"
              name="address.neighbourhood"
              value={formData.address.neighbourhood || ''}
              onChange={handleChange}
              placeholder="Bairro"
            />
          </div>

          <div className="form-group">
            <label>Cidade*</label>
            <input
              type="text"
              name="address.city"
              value={formData.address.city}
              onChange={handleChange}
              required
              placeholder="Cidade"
              className={errors['address.city'] ? 'error' : ''}
            />
            {errors['address.city'] && <div className="error-message">{errors['address.city']}</div>}
          </div>

          <div className="form-group">
            <label>Estado*</label>
            <select
              name="address.state"
              value={formData.address.state}
              onChange={handleChange}
              required
              className={errors['address.state'] ? 'error' : ''}
            >
              <option value="">Selecione um estado</option>
              <option value="AC">Acre</option>
              <option value="AL">Alagoas</option>
              <option value="AP">Amapá</option>
              <option value="AM">Amazonas</option>
              <option value="BA">Bahia</option>
              <option value="CE">Ceará</option>
              <option value="DF">Distrito Federal</option>
              <option value="ES">Espírito Santo</option>
              <option value="GO">Goiás</option>
              <option value="MA">Maranhão</option>
              <option value="MT">Mato Grosso</option>
              <option value="MS">Mato Grosso do Sul</option>
              <option value="MG">Minas Gerais</option>
              <option value="PA">Pará</option>
              <option value="PB">Paraíba</option>
              <option value="PR">Paraná</option>
              <option value="PE">Pernambuco</option>
              <option value="PI">Piauí</option>
              <option value="RJ">Rio de Janeiro</option>
              <option value="RN">Rio Grande do Norte</option>
              <option value="RS">Rio Grande do Sul</option>
              <option value="RO">Rondônia</option>
              <option value="RR">Roraima</option>
              <option value="SC">Santa Catarina</option>
              <option value="SP">São Paulo</option>
              <option value="SE">Sergipe</option>
              <option value="TO">Tocantins</option>
            </select>
            {errors['address.state'] && <div className="error-message">{errors['address.state']}</div>}
          </div>

          <div className="form-group">
            <label>CEP*</label>
            <input
              type="text"
              name="address.zipCode"
              value={formData.address.zipCode}
              onChange={handleChange}
              required
              placeholder="XXXXX-XXX"
              maxLength={9}
              className={errors['address.zipCode'] ? 'error' : ''}
            />
            {errors['address.zipCode'] && <div className="error-message">{errors['address.zipCode']}</div>}
          </div>
        </div>
      )}
      
      {activeTab === 'services' && (
        <div className="tab-content">
          <h3>Serviços oferecidos pelo profissional</h3>
          
          {services.length === 0 ? (
            <div className="no-data">
              Nenhum serviço cadastrado para esta subsidiária.
            </div>
          ) : (
            <div className="professionals-list">
              {services.map(service => (
                <div key={service.id} className="service-item">
                  <div className="service-header">
                    <input
                      type="checkbox"
                      id={`service-${service.id}`}
                      checked={selectedServices.includes(service.id!)}
                      onChange={() => handleServiceToggle(service.id!)}
                    />
                    <label htmlFor={`service-${service.id}`}>
                      {service.name} - {service.price.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })} ({service.durationMinutes} min)
                    </label>
                  </div>
                  
                  {selectedServices.includes(service.id!) && (
                    <div className="service-config">
                      <div className="form-group">
                        <label>Tipo de Comissão</label>
                        <select
                          value={serviceConfigs.find(c => c.serviceId === service.id)?.commissionType || 'PERCENTAGE'}
                          onChange={(e) => handleServiceConfigChange(service.id!, 'commissionType', e.target.value)}
                        >
                          <option value="PERCENTAGE">Porcentagem</option>
                          <option value="FIXED">Valor Fixo</option>
                          <option value="NONE">Sem Comissão</option>
                        </select>
                      </div>
                      
                      <div className="form-group">
                        <label>Valor da Comissão</label>
                        <input
                          type="number"
                          step="0.01"
                          min="0"
                          value={serviceConfigs.find(c => c.serviceId === service.id)?.commissionValue || 0}
                          onChange={(e) => handleServiceConfigChange(service.id!, 'commissionValue', parseFloat(e.target.value))}
                        />
                      </div>
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      )}
      
      {activeTab === 'schedule' && (
        <div className="tab-content">
          {!initialData.id ? (
            <div className="no-data">
              Salve primeiro o profissional para gerenciar agenda.
            </div>
          ) : (
            <div className="schedule-section">
              <h3>Agenda do Profissional</h3>
              <p>Funcionalidade em desenvolvimento.</p>
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