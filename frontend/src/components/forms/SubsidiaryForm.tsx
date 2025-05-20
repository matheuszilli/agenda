import { useState, useEffect } from 'react';
import type { Subsidiary } from '../../services/subsidiaryService';
import type { Company } from '../../services/companyService';
import { companyService } from '../../services/companyService';
import './CompanyForm.css';  // Reutilizando os estilos

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
        city: '',
        state: '',
        zipCode: ''
    },
    documentNumber: '',
    companyId: ''
};

export default function SubsidiaryForm({ initialData = emptySubsidiary, onSubmit, onCancel }: SubsidiaryFormProps) {
    const [formData, setFormData] = useState<Subsidiary>(initialData);
    const [companies, setCompanies] = useState<Company[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

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

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;

        if (name.includes('.')) {
            const [parent, child] = name.split('.');
            setFormData({
                ...formData,
                [parent]: {
                    ...formData[parent as keyof Subsidiary] as Record<string, any>,
                    [child]: value
                }
            });
        } else {
            setFormData({
                ...formData,
                [name]: value
            });
        }
    };

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSubmit(formData);
    };

    if (loading) return <div>Carregando empresas...</div>;
    if (error) return <div className="error-message">{error}</div>;
    if (companies.length === 0) return <div>Nenhuma empresa cadastrada. Por favor, cadastre uma empresa primeiro.</div>;

    return (
        <form onSubmit={handleSubmit} className="company-form">
            <h2 className="form-section-header">{initialData.id ? 'Editar Subsidiária' : 'Nova Subsidiária'}</h2>

            <div className="form-group full-width">
                <label>Empresa</label>
                <select
                    name="companyId"
                    value={formData.companyId}
                    onChange={handleChange}
                    required
                >
                    <option value="">Selecione uma empresa</option>
                    {companies.map(company => (
                        <option key={company.id} value={company.id}>
                            {company.name}
                        </option>
                    ))}
                </select>
            </div>

            <div className="form-group full-width">
                <label>Nome</label>
                <input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    required
                />
            </div>

            <div className="form-group">
                <label>CNPJ</label>
                <input
                    type="text"
                    name="documentNumber"
                    value={formData.documentNumber}
                    onChange={handleChange}
                    required
                />
            </div>

            <h3 className="form-section-header">Endereço</h3>

            <div className="form-group">
                <label>Rua</label>
                <input
                    type="text"
                    name="address.street"
                    value={formData.address.street}
                    onChange={handleChange}
                    required
                />
            </div>

            <div className="form-group">
                <label>Número</label>
                <input
                    type="text"
                    name="address.number"
                    value={formData.address.number}
                    onChange={handleChange}
                    required
                />
            </div>

            <div className="form-group">
                <label>Cidade</label>
                <input
                    type="text"
                    name="address.city"
                    value={formData.address.city}
                    onChange={handleChange}
                    required
                />
            </div>

            <div className="form-group">
                <label>Estado</label>
                <input
                    type="text"
                    name="address.state"
                    value={formData.address.state}
                    onChange={handleChange}
                    required
                />
            </div>

            <div className="form-group">
                <label>CEP</label>
                <input
                    type="text"
                    name="address.zipCode"
                    value={formData.address.zipCode}
                    onChange={handleChange}
                    required
                />
            </div>

            <div className="form-buttons">
                <button type="button" className="secondary" onClick={onCancel}>Cancelar</button>
                <button type="submit">Salvar</button>
            </div>
        </form>
    );
}