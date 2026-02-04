export interface ProjectRequest {
  name: string;
  description?: string;
  sector: string;
  client: string;
  clientContact: string;
  projectManager: string;
  collaborators?: string[];
}

export interface ProjectResponse {
  id: number;
  name: string;
  description?: string;
  sector: string;
  client: string;
  clientContact: string;
  projectManager: string;
  owner: string;
  collaborators?: string[];
  createdAt: string;
  updatedAt: string;
}
