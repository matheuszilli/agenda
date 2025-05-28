import { useState, useEffect } from 'react';
import type { Company } from '../../services/companyService';
import { companyService } from '../../services/companyService';
import './CompanyForm.css';

// Máscara de formatação para CNPJ
const formatCNPJ = (value: string) => {
  if (!value) return '';
  // Remove tudo que não é dígito
  const digits = value.replace(/\D/g, '');
  
  // Aplica máscara XX.XXX.XXX/XXXX-XX
  return digits
    .replace(/(\d{2})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1/$2')
    .replace(/(\d{4})(\d)/, '$1-$2')
    .substring(0, 18);
};

// Máscara de formatação para Telefone
const formatPhone = (value: string) => {
  if (!value) return '';
  // Remove tudo que não é dígito
  const digits = value.replace(/\D/g, '');
  
  // Aplica máscara (XX) XXXXX-XXXX ou (XX) XXXX-XXXX
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

interface CompanyFormProps {
    initialData?: Company;
    onSubmit: (data: Company) => void;
    onCancel: () => void;
}

const emptyCompany: Company = {
    name: '',
    tradingName: '',
    address: {
        street: '',
        number: '',
        complement: '',
        city: '',
        state: '',
        zipCode: ''
    },
    phone: '',
    documentNumber: '',
    typeOfDocument: 'CNPJ'
};

export default function CompanyForm({ initialData = emptyCompany, onSubmit, onCancel }: CompanyFormProps) {
    const [formData, setFormData] = useState<Company>(initialData);
    const [activeTab, setActiveTab] = useState('company-info');
    
    // Para informações de erro de validação
    const [errors, setErrors] = useState<Record<string, string>>({});
    
    // Para gerenciar o estado das subsidiárias
    const [subsidiaries, setSubsidiaries] = useState<any[]>([]);
    const [loadingSubsidiaries, setLoadingSubsidiaries] = useState(false);
    const [subsidiaryError, setSubsidiaryError] = useState('');

    // Carregar subsidiárias se existir um ID de empresa
    useEffect(() => {
        if (initialData.id) {
            fetchSubsidiaries(initialData.id);
        }
    }, [initialData.id]);

    // Função para buscar subsidiárias da API
    const fetchSubsidiaries = async (companyId: string) => {
        try {
            setLoadingSubsidiaries(true);
            setSubsidiaryError('');
            // Assumindo que existe um método no serviço para buscar subsidiárias
            const data = await companyService.getSubsidiaries(companyId);
            setSubsidiaries(data);
        } catch (error: any) {
            setSubsidiaryError(`Erro ao carregar subsidiárias: ${error.message || 'Erro desconhecido'}`);
            console.error('Erro ao carregar subsidiárias:', error);
        } finally {
            setLoadingSubsidiaries(false);
        }
    };

    const validateForm = () => {
        const newErrors: Record<string, string> = {};
        
        // Validar campos obrigatórios da empresa
        if (!formData.name) newErrors.name = "O nome da empresa é obrigatório";
        if (!formData.tradingName) newErrors.tradingName = "O nome fantasia é obrigatório";
        if (!formData.documentNumber) newErrors.documentNumber = "O CNPJ é obrigatório";
        if (!formData.typeOfDocument) newErrors.typeOfDocument = "O tipo de documento é obrigatório";
        
        // Validar campos obrigatórios do endereço
        if (!formData.address.street) newErrors['address.street'] = "A rua é obrigatória";
        if (!formData.address.number) newErrors['address.number'] = "O número é obrigatório";
        if (!formData.address.city) newErrors['address.city'] = "A cidade é obrigatória";
        if (!formData.address.state) newErrors['address.state'] = "O estado é obrigatório";
        if (!formData.address.zipCode) newErrors['address.zipCode'] = "O CEP é obrigatório";
        
        // Validações de formato
        const cnpjDigits = formData.documentNumber.replace(/\D/g, '');
        if (cnpjDigits.length > 0 && cnpjDigits.length !== 14) {
            newErrors.documentNumber = "CNPJ inválido. Deve ter 14 dígitos";
        }
        
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const {name, value} = e.target;

        // Aplicar máscaras de formatação quando necessário
        let formattedValue = value;
        if (name === 'documentNumber') {
            formattedValue = formatCNPJ(value);
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
                    ...formData[parent as keyof Company] as Record<string, any>,
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

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (validateForm()) {
            onSubmit(formData);
        }
    };

    // Manipuladores para ações de subsidiárias
    const handleAddSubsidiary = () => {
        // Aqui você redirecionaria para o formulário de subsidiária
        // ou abriria um modal para criar uma nova subsidiária
        alert('Adicionar subsidiária - implementar navegação para o formulário de subsidiária');
    };

    const handleEditSubsidiary = (subsidiaryId: string) => {
        // Abrir o formulário de edição de subsidiária
        alert(`Editar subsidiária ${subsidiaryId} - implementar navegação para o formulário de subsidiária`);
    };

    const handleDeleteSubsidiary = async (subsidiaryId: string) => {
        if (!window.confirm('Tem certeza que deseja excluir esta subsidiária?')) {
            return;
        }

        try {
            // Assumindo que existe um método no serviço para excluir subsidiária
            await companyService.deleteSubsidiary(subsidiaryId);
            // Recarregar a lista após a exclusão
            if (initialData.id) {
                fetchSubsidiaries(initialData.id);
            }
        } catch (error: any) {
            setSubsidiaryError(`Erro ao excluir subsidiária: ${error.message || 'Erro desconhecido'}`);
            console.error('Erro ao excluir subsidiária:', error);
        }
    };

    return (
        <form onSubmit={handleSubmit} className="company-form">
            <h2 className="form-section-header">{initialData.id ? 'Editar Empresa' : 'Nova Empresa'}</h2>
            
            <div className="tabs">
                <button 
                    type="button"
                    className={`tab-button ${activeTab === 'company-info' ? 'active' : ''}`}
                    onClick={() => setActiveTab('company-info')}
                >
                    Informações da Empresa
                </button>
                <button 
                    type="button"
                    className={`tab-button ${activeTab === 'subsidiaries' ? 'active' : ''}`}
                    onClick={() => setActiveTab('subsidiaries')}
                    disabled={!initialData.id}
                >
                    Filiais / Subsidiárias
                </button>
            </div>
            
            {activeTab === 'company-info' && (
                <div className="tab-content">
                    <div className="form-group full-width">
                        <label>Nome da Empresa*</label>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            required
                            className={errors.name ? 'error' : ''}
                        />
                        {errors.name && <div className="error-message">{errors.name}</div>}
                    </div>

                    <div className="form-group full-width">
                        <label>Nome Fantasia*</label>
                        <input
                            type="text"
                            name="tradingName"
                            value={formData.tradingName}
                            onChange={handleChange}
                            required
                            className={errors.tradingName ? 'error' : ''}
                        />
                        {errors.tradingName && <div className="error-message">{errors.tradingName}</div>}
                    </div>

                    <div className="form-group">
                        <label>Tipo de Documento*</label>
                        <select
                            name="typeOfDocument"
                            value={formData.typeOfDocument}
                            onChange={handleChange}
                            required
                            className={errors.typeOfDocument ? 'error' : ''}
                        >
                            <option value="">Selecione o tipo</option>
                            <option value="CNPJ">CNPJ</option>
                            <option value="CPF">CPF</option>
                        </select>
                        {errors.typeOfDocument && <div className="error-message">{errors.typeOfDocument}</div>}
                    </div>

                    <div className="form-group">
                        <label>CNPJ*</label>
                        <input
                            type="text"
                            name="documentNumber"
                            value={formData.documentNumber}
                            onChange={handleChange}
                            required
                            placeholder="XX.XXX.XXX/XXXX-XX"
                            className={errors.documentNumber ? 'error' : ''}
                            maxLength={18}
                        />
                        {errors.documentNumber && <div className="error-message">{errors.documentNumber}</div>}
                    </div>

                    <div className="form-group">
                        <label>Telefone</label>
                        <input
                            type="text"
                            name="phone"
                            value={formData.phone}
                            onChange={handleChange}
                            placeholder="(XX) XXXXX-XXXX"
                            maxLength={15}
                        />
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
                        />
                    </div>

                    <div className="form-group">
                        <label>Bairro</label>
                        <input
                            type="text"
                            name="address.neighbourhood"
                            value={formData.address.neighbourhood || ''}
                            onChange={handleChange}
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
            
            {activeTab === 'subsidiaries' && (
                <div className="tab-content">
                    {!initialData.id ? (
                        <div className="no-subsidiaries-message">
                            Salve primeiro a empresa para gerenciar subsidiárias.
                        </div>
                    ) : (
                        <div className="subsidiaries-panel">
                            <div className="panel-header">
                                <h3>Subsidiárias</h3>
                                <button 
                                    type="button" 
                                    className="add-button"
                                    onClick={handleAddSubsidiary}
                                >
                                    + Nova Subsidiária
                                </button>
                            </div>
                            
                            {subsidiaryError && (
                                <div className="error-message">{subsidiaryError}</div>
                            )}
                            
                            {loadingSubsidiaries ? (
                                <div className="loading-message">Carregando subsidiárias...</div>
                            ) : subsidiaries.length === 0 ? (
                                <div className="no-data">
                                    Nenhuma subsidiária encontrada para esta empresa.
                                    <br />
                                    Clique em "Nova Subsidiária" para adicionar.
                                </div>
                            ) : (
                                <table className="subsidiaries-table">
                                    <thead>
                                        <tr>
                                            <th>Nome</th>
                                            <th>CNPJ</th>
                                            <th>Endereço</th>
                                            <th>Ações</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {subsidiaries.map(sub => (
                                            <tr key={sub.id}>
                                                <td>{sub.name}</td>
                                                <td>{sub.documentNumber}</td>
                                                <td>{sub.address?.city}/{sub.address?.state}</td>
                                                <td className="action-buttons">
                                                    <button 
                                                        type="button" 
                                                        className="edit-button"
                                                        onClick={() => handleEditSubsidiary(sub.id)}
                                                    >
                                                        Editar
                                                    </button>
                                                    <button 
                                                        type="button" 
                                                        className="delete-button"
                                                        onClick={() => handleDeleteSubsidiary(sub.id)}
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