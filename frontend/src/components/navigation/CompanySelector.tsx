import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { type Company, companyService } from '../../services/companyService';
import { useCompany } from '../../contexts/CompanyContext';
import './CompanySelector.css';

export default function CompanySelector() {
  const [companies, setCompanies] = useState<Company[]>([]);
  const [subsidiaries, setSubsidiaries] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const { selectedCompany, setSelectedCompany, selectedSubsidiary, setSelectedSubsidiary } = useCompany();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCompanies = async () => {
      try {
        setLoading(true);
        const data = await companyService.getAll();
        setCompanies(data);
        
        // Se não houver empresa selecionada e houver empresas disponíveis
        if (!selectedCompany && data.length > 0) {
          setSelectedCompany(data[0]);
          
          // Buscar subsidiárias para a empresa selecionada
          fetchSubsidiaries(data[0].id!);
        } else if (selectedCompany) {
          // Se já temos uma empresa selecionada, buscar suas subsidiárias
          fetchSubsidiaries(selectedCompany.id!);
        }
      } catch (err: any) {
        setError(`Erro ao carregar empresas: ${err.message}`);
        console.error('Erro:', err);
      } finally {
        setLoading(false);
      }
    };
    
    fetchCompanies();
  }, []);
  
  const fetchSubsidiaries = async (companyId: string) => {
    try {
      const data = await companyService.getSubsidiaries(companyId);
      setSubsidiaries(data);
      
      // Se não houver subsidiária selecionada e houver subsidiárias disponíveis
      if (!selectedSubsidiary && data.length > 0) {
        setSelectedSubsidiary(data[0]);
      }
    } catch (err) {
      console.error('Erro ao carregar subsidiárias:', err);
    }
  };

  const handleCompanyChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const companyId = e.target.value;
    const company = companies.find(c => c.id === companyId);
    
    if (company) {
      setSelectedCompany(company);
      setSelectedSubsidiary(null);
      fetchSubsidiaries(companyId);
    }
  };
  
  const handleSubsidiaryChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const subsidiaryId = e.target.value;
    const subsidiary = subsidiaries.find(s => s.id === subsidiaryId);
    
    if (subsidiary) {
      setSelectedSubsidiary(subsidiary);
    }
  };

  const handleAddCompany = () => {
    navigate('/empresas/nova');
  };

  if (loading) return <div className="company-selector-loading">Carregando...</div>;
  if (error) return <div className="company-selector-error">{error}</div>;
  if (companies.length === 0) {
    return (
      <div className="company-selector-empty">
        <span>Nenhuma empresa cadastrada</span>
        <button onClick={handleAddCompany}>+ Adicionar Empresa</button>
      </div>
    );
  }

  return (
    <div className="company-selector">
      <div className="selector-group">
        <label>Empresa:</label>
        <select 
          value={selectedCompany?.id || ''}
          onChange={handleCompanyChange}
        >
          {companies.map(company => (
            <option key={company.id} value={company.id}>
              {company.name}
            </option>
          ))}
        </select>
      </div>
      
      {subsidiaries.length > 0 && (
        <div className="selector-group">
          <label>Subsidiária:</label>
          <select 
            value={selectedSubsidiary?.id || ''}
            onChange={handleSubsidiaryChange}
          >
            <option value="">Todas</option>
            {subsidiaries.map(subsidiary => (
              <option key={subsidiary.id} value={subsidiary.id}>
                {subsidiary.name}
              </option>
            ))}
          </select>
        </div>
      )}
    </div>
  );
}