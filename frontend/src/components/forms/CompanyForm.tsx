import { useState } from 'react';
import type { Company } from '../../services/companyService';

interface CompanyFormProps {
    initialData?: Company;
    onSubmit: (data: Company) => void;
    onCancel: () => void;
}

const emptyCompany: Company = {
    name: '',
    address: {
        street: '',
        number: '',
        city: '',
        state: '',
        zipCode: ''
    },
    phone: '',
    documentNumber: ''
};

export default function CompanyForm({ initialData = emptyCompany, onSubmit, onCancel }: CompanyFormProps) {
    const [formData, setFormData] = useState<Company>(initialData);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const {name, value} = e.target;

        if (name.includes('.')) {
            const [parent, child] = name.split('.');
            setFormData({
                ...formData,
                [parent]: {
                    ...formData[parent as keyof Company] as Record<string, any>,
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

    return (
        <form onSubmit={handleSubmit} className="company-form">
            <h2 className="form-section-header">{initialData.id ? 'Editar Empresa' : 'Nova Empresa'}</h2>

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

            <div className="form-group">
                <label>Telefone</label>
                <input
                    type="text"
                    name="phone"
                    value={formData.phone}
                    onChange={handleChange}
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