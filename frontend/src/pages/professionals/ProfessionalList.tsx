import { useState, useEffect } from 'react';
import api from '../../services/api';

 interface Professional {
  id: string;
  fullName?: string;
  documentNumber: string;
  phone: string;
}

export default function ProfessionalList() {
  const [professionals, setProfessionals] = useState<Professional[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchProfessionals = async () => {
      try {
        const response = await api.get('/professionals');
        setProfessionals(response.data);
        setLoading(false);
      } catch (err) {
        setError('Erro ao carregar profissionais');
        setLoading(false);
        console.error('Erro:', err);
      }
    };

    fetchProfessionals();
  }, []);

  if (loading) return <div>Carregando...</div>;
  if (error) return <div>{error}</div>;

  return (
    <div>
      <h1>Profissionais</h1>
      {professionals.length === 0 ? (
        <p>Nenhum profissional encontrado</p>
      ) : (
        <ul>
          {professionals.map(professional => (
            <li key={professional.id}>
              {professional.fullName || 'Nome não informado'} - {professional.documentNumber} - {professional.phone}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}