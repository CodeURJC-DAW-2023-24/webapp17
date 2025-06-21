import { User } from './user';
import { Comment } from './comment';

export interface Post {
  id: number;
  user?: User;
  comments?: Comment[];
  title?: string;
  image?: string;
  content?: string;
  date?: string;   
  tag?: string;
}

export interface PostCreationDTO {
  title: string;
  content: string;
  tag: string;
  image: File | null; 
}

export interface PostPage {
  content: Post[];
  totalPages: number;
  first: boolean;
  last: boolean;
}