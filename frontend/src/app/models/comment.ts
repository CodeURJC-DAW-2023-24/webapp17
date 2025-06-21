import { User } from './user';
import { Post } from './post';

export interface Comment {
  id: number;
  user: User;
  post: Post;
  date: string; 
  text: string;
}
