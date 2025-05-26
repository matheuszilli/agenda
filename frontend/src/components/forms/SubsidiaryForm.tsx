import { useState, useEffect } from 'react';
import type { Subsidiary } from '../../services/subsidiaryService';
import { subsidiaryService } from '../../services/subsidiaryService';
import type { Company } from '../../services/companyService';
import { companyService } from '../../services/companyService';
import { chairRoomService } from '../../services/chairRoomService';
import { professionalService } from '../../services/professionalService';
import './CompanyForm.css';  // Reutilizando os estilos

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

interface SubsidiaryFormProps {
    initialData?: Subsidiary;
    onSubmit: (data: Subsidiary) => void;
    onCancel: () => void;
}

const emptySubsidiary: Subsidiary = {
    name: '',
    address: {
        street: '',
        number: '',
        complement: '',
        neighbourhood: '',
        city: '',
        state: '',
        zipCode: ''
    },
    documentNumber: '',
    companyId: ''
};

interface Professional {
    id: string;
    name: string;
    email: string;
    phone: string;
    documentNumber: string;
    subsidiaryId: string;
}

export default function SubsidiaryForm({ initialData = emptySubsidiary, onSubmit, onCancel }: SubsidiaryFormProps) {
    const [formData, setFormData] = useState<Subsidiary>(initialData);
    const [companies, setCompanies] = useState<Company[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    const [activeTab, setActiveTab] = useState('subsidiary-info');
    
    // Para informações de erro de validação
    const [errors, setErrors] = useState<Record<string, string>>({});
    
    // Para gerenciar o estado das entidades relacionadas
    const [chairRooms, setChairRooms] = useState<any[]>([]);
    const [professionals, setProfessionals] = useState<any[]>([]);
    const [services, setServices] = useState<any[]>([]);
    const [schedules, setSchedules] = useState<any[]>([]);
    const [loadingRelated, setLoadingRelated] = useState(false);
    const [relatedError, setRelatedError] = useState('');

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

    useEffect(() => {
        if (!initialData.id) return;  // só busca se já tivermos o ID
    
        const fetchProfessionals = async () => {
            try {
                setLoading(true);
                // passa o ID correto para o serviço
                const data = await professionalService.getBySubsidiary(initialData.id!);
                setProfessionals(data);
            } catch (err: any) {
                setRelatedError('Erro ao carregar profissionais');
                console.error('Erro ao carregar profissionais:', err);
            } finally {
                setLoading(false);
            }
        };
    
        fetchProfessionals();
    }, [initialData.id]);

    // Carregar entidades relacionadas quando já existe um ID de subsidiária
    useEffect(() => {
        if (initialData.id) {
            fetchRelatedEntities(initialData.id);
        }
    }, [initialData.id]);

    // Função para buscar entidades relacionadas
    const fetchRelatedEntities = async (subsidiaryId: string) => {
        setLoadingRelated(true);
        setRelatedError('');

        try {
            // Cadeiras/Salas
            const chairRoomsData = await chairRoomService.getBySubsidiary(subsidiaryId);
            setChairRooms(chairRoomsData);

            // Horários (implementar quando o serviço estiver disponível)
            // const schedulesData = await subsidiaryService.getSchedules(subsidiaryId);
            // setSchedules(schedulesData);

            // Profissionais (a implementar)
            const professionalsData = await professionalService.getBySubsidiary(subsidiaryId);
            setProfessionals(professionalsData);


            // Serviços (a implementar)
            // const servicesData = await serviceService.getBySubsidiary(subsidiaryId);
            // setServices(servicesData);
        } catch (error: any) {
            setRelatedError(`Erro ao carregar dados relacionados: ${error.message || 'Erro desconhecido'}`);
            console.error('Erro ao carregar dados relacionados:', error);
        } finally {
            setLoadingRelated(false);
        }
    };

    const validateForm = () => {
        const newErrors: Record<string, string> = {};
        
        // Validar campos obrigatórios da subsidiária
        if (!formData.name) newErrors.name = "O nome da subsidiária é obrigatório";
        if (!formData.documentNumber) newErrors.documentNumber = "O CNPJ é obrigatório";
        if (!formData.companyId) newErrors.companyId = "A empresa é obrigatória";
        
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
        } else if (name === 'address.zipCode') {
            formattedValue = formatCEP(value);
        }

        if (name.includes('.')) {
            const [parent, child] = name.split('.');
            setFormData({
                ...formData,
                [parent]: {
                    ...formData[parent as keyof Subsidiary] as Record<string, any>,
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

    // Manipuladores para ações de cadeiras/salas
    const handleAddChairRoom = () => {
        alert('Adicionar cadeira/sala - implementar navegação para o formulário de cadeira/sala');
    };

    const handleEditChairRoom = (chairRoomId: string) => {
        alert(`Editar cadeira/sala ${chairRoomId} - implementar navegação para o formulário de cadeira/sala`);
    };

    const handleDeleteChairRoom = async (chairRoomId: string) => {
        if (!window.confirm('Tem certeza que deseja excluir esta cadeira/sala?')) {
            return;
        }

        try {
            await chairRoomService.delete(chairRoomId);
            // Recarregar a lista após a exclusão
            if (initialData.id) {
                fetchRelatedEntities(initialData.id);
            }
        } catch (error: any) {
            setRelatedError(`Erro ao excluir cadeira/sala: ${error.message || 'Erro desconhecido'}`);
            console.error('Erro ao excluir cadeira/sala:', error);
        }
    };

    // Manipuladores para horários (a implementar)
    const handleAddSchedule = () => {
        alert('Adicionar horário - implementar formulário de horários recorrentes');
    };

    if (loading) return <div>Carregando empresas...</div>;
    if (error) return <div className="error-message">{error}</div>;
    if (companies.length === 0) return <div>Nenhuma empresa cadastrada. Por favor, cadastre uma empresa primeiro.</div>;

    return (
        <form onSubmit={handleSubmit} className="company-form">
            <h2 className="form-section-header">{initialData.id ? 'Editar Subsidiária' : 'Nova Subsidiária'}</h2>
            
            <div className="tabs">
                <button 
                    type="button"
                    className={`tab-button ${activeTab === 'subsidiary-info' ? 'active' : ''}`}
                    onClick={() => setActiveTab('subsidiary-info')}
                >
                    Informações da Subsidiária
                </button>
                <button 
                    type="button"
                    className={`tab-button ${activeTab === 'chair-rooms' ? 'active' : ''}`}
                    onClick={() => setActiveTab('chair-rooms')}
                    disabled={!initialData.id}
                >
                    Cadeiras/Salas
                </button>
                <button 
                    type="button"
                    className={`tab-button ${activeTab === 'professionals' ? 'active' : ''}`}
                    onClick={() => setActiveTab('professionals')}
                    disabled={!initialData.id}
                >
                    Profissionais
                </button>
                <button 
                    type="button"
                    className={`tab-button ${activeTab === 'services' ? 'active' : ''}`}
                    onClick={() => setActiveTab('services')}
                    disabled={!initialData.id}
                >
                    Serviços
                </button>
                <button 
                    type="button"
                    className={`tab-button ${activeTab === 'schedules' ? 'active' : ''}`}
                    onClick={() => setActiveTab('schedules')}
                    disabled={!initialData.id}
                >
                    Horários
                </button>
            </div>
            
            {activeTab === 'subsidiary-info' && (
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
                        <label>Nome da Subsidiária*</label>
                        <input
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            required
                            className={errors.name ? 'error' : ''}
                            placeholder="Nome da filial"
                        />
                        {errors.name && <div className="error-message">{errors.name}</div>}
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
            
            {activeTab === 'chair-rooms' && (
                <div className="tab-content">
                    {!initialData.id ? (
                        <div className="no-subsidiaries-message">
                            Salve primeiro a subsidiária para gerenciar cadeiras/salas.
                        </div>
                    ) : (
                        <div className="subsidiaries-panel">
                            <div className="panel-header">
                                <h3>Cadeiras/Salas</h3>
                                <button 
                                    type="button" 
                                    className="add-button"
                                    onClick={handleAddChairRoom}
                                >
                                    + Nova Cadeira/Sala
                                </button>
                            </div>
                            
                            {relatedError && (
                                <div className="error-message">{relatedError}</div>
                            )}
                            
                            {loadingRelated ? (
                                <div className="loading-message">Carregando cadeiras/salas...</div>
                            ) : chairRooms.length === 0 ? (
                                <div className="no-data">
                                    Nenhuma cadeira/sala encontrada para esta subsidiária.
                                    <br />
                                    Clique em "Nova Cadeira/Sala" para adicionar.
                                </div>
                            ) : (
                                <table className="subsidiaries-table">
                                    <thead>
                                        <tr>
                                            <th>Nome</th>
                                            <th>Número</th>
                                            <th>Capacidade</th>
                                            <th>Ações</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {chairRooms.map(chair => (
                                            <tr key={chair.id}>
                                                <td>{chair.name}</td>
                                                <td>{chair.roomNumber}</td>
                                                <td>{chair.capacity}</td>
                                                <td className="action-buttons">
                                                    <button 
                                                        type="button" 
                                                        className="edit-button"
                                                        onClick={() => handleEditChairRoom(chair.id)}
                                                    >
                                                        Editar
                                                    </button>
                                                    <button 
                                                        type="button" 
                                                        className="delete-button"
                                                        onClick={() => handleDeleteChairRoom(chair.id)}
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
            
            {activeTab === 'professionals' && (
                <div className="tab-content">
                    {!initialData.id ? (
                        <div className="no-subsidiaries-message">
                            Salve primeiro a subsidiária para gerenciar profissionais.
                        </div>
                    ) : (
                        <div className="subsidiaries-panel">
                            <div className="panel-header">
                                <h3>Profissionais</h3>
                                <button 
                                    type="button" 
                                    className="add-button"
                                    onClick={() => alert('Adicionar profissional - a implementar')}
                                >
                                    + Novo Profissional
                                </button>
                            </div>

                        </div>
                    )}
                </div>
            )}
            
            {activeTab === 'services' && (
                <div className="tab-content">
                    {!initialData.id ? (
                        <div className="no-subsidiaries-message">
                            Salve primeiro a subsidiária para gerenciar serviços.
                        </div>
                    ) : (
                        <div className="subsidiaries-panel">
                            <div className="panel-header">
                                <h3>Serviços</h3>
                                <button 
                                    type="button" 
                                    className="add-button"
                                    onClick={() => alert('Adicionar serviço - a implementar')}
                                >
                                    + Novo Serviço
                                </button>
                            </div>
                            
                            <div className="no-data">
                                Funcionalidade em desenvolvimento.
                            </div>
                        </div>
                    )}
                </div>
            )}
            
            {activeTab === 'schedules' && (
                <div className="tab-content">
                    {!initialData.id ? (
                        <div className="no-subsidiaries-message">
                            Salve primeiro a subsidiária para gerenciar horários.
                        </div>
                    ) : (
                        <div className="subsidiaries-panel">
                            <div className="panel-header">
                                <h3>Horários de Funcionamento</h3>
                                <button 
                                    type="button" 
                                    className="add-button"
                                    onClick={handleAddSchedule}
                                >
                                    + Definir Horários
                                </button>
                            </div>
                            
                            {relatedError && (
                                <div className="error-message">{relatedError}</div>
                            )}
                            
                            {loadingRelated ? (
                                <div className="loading-message">Carregando horários...</div>
                            ) : schedules && schedules.length > 0 ? (
                                <table className="subsidiaries-table">
                                    <thead>
                                        <tr>
                                            <th>Data</th>
                                            <th>Horário Abertura</th>
                                            <th>Horário Fechamento</th>
                                            <th>Fechado</th>
                                            <th>Ações</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {schedules.map(schedule => (
                                            <tr key={schedule.id}>
                                                <td>{new Date(schedule.date).toLocaleDateString()}</td>
                                                <td>{schedule.openTime}</td>
                                                <td>{schedule.closeTime}</td>
                                                <td>{schedule.closed ? 'Sim' : 'Não'}</td>
                                                <td className="action-buttons">
                                                    <button 
                                                        type="button" 
                                                        className="edit-button"
                                                        onClick={() => alert(`Editar horário ${schedule.id} - a implementar`)}
                                                    >
                                                        Editar
                                                    </button>
                                                    <button 
                                                        type="button" 
                                                        className="delete-button"
                                                        onClick={() => alert(`Excluir horário ${schedule.id} - a implementar`)}
                                                    >
                                                        Excluir
                                                    </button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            ) : (
                                <div className="no-data">
                                    Nenhum horário definido para esta subsidiária.
                                    <br />
                                    Clique em "Definir Horários" para adicionar.
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