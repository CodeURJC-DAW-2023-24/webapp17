import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpEvent, HttpEventType } from '@angular/common/http';
import { Observable, map, catchError, of } from 'rxjs';

// Interface for upload progress
export interface UploadProgress {
  progress: number;
  loaded: number;
  total: number;
  status: 'uploading' | 'completed' | 'error';
}

// Interface for upload response
export interface ImageUploadResponse {
  imageUrl?: string;
  error?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ImageService {
  private readonly baseUrl = 'https://localhost:8443/api/images'; // Adjust the URL according to your configuration
  
  constructor(private http: HttpClient) { }

  /**
   * Uploads an image file to the server
   * @param file The image file to upload
   * @returns Observable<string> The URL of the uploaded image or error message
   */
  uploadImage(file: File): Observable<string> {
    if (!file) {
      throw new Error('No file provided');
    }

    // Validate file type
    if (!this.isValidImageFile(file)) {
      throw new Error('Invalid file type. Only image files are allowed.');
    }

    const formData = new FormData();
    formData.append('image', file);

    return this.http.post<string>(this.baseUrl, formData, {
      withCredentials: true,
      responseType: 'text' as 'json' // Handle plain text response
    });
  }

  /**
   * Uploads an image with progress tracking
   * @param file The image file to upload
   * @returns Observable<UploadProgress | ImageUploadResponse> Progress updates and final result
   */
  uploadImageWithProgress(file: File): Observable<UploadProgress | ImageUploadResponse> {
    if (!file) {
      throw new Error('No file provided');
    }

    if (!this.isValidImageFile(file)) {
      throw new Error('Invalid file type. Only image files are allowed.');
    }

    const formData = new FormData();
    formData.append('image', file);

    return this.http.post(this.baseUrl, formData, {
      withCredentials: true,
      reportProgress: true,
      observe: 'events',
      responseType: 'text'
    }).pipe(
      map((event: HttpEvent<any>) => {
        switch (event.type) {
          case HttpEventType.UploadProgress:
            const progress = Math.round(100 * event.loaded / (event.total || 1));
            return {
              progress,
              loaded: event.loaded,
              total: event.total || 0,
              status: 'uploading' as const
            };
          case HttpEventType.Response:
            if (event.status === 200) {
              return {
                imageUrl: event.body,
                status: 'completed' as const
              } as ImageUploadResponse;
            } else {
              return {
                error: event.body || 'Upload failed',
                status: 'error' as const
              } as ImageUploadResponse;
            }
          default:
            return {
              progress: 0,
              loaded: 0,
              total: 0,
              status: 'uploading' as const
            };
        }
      })
    );
  }

  /**
   * Gets the full URL for an image
   * @param fileName The name of the image file
   * @returns string Full URL to access the image
   */
  getImageUrl(fileName: string): string {
    if (!fileName) {
      return '';
    }
    
    // Remove leading slash if present in fileName
    const cleanFileName = fileName.startsWith('/images/') 
      ? fileName.substring(8) 
      : fileName.startsWith('/') 
        ? fileName.substring(1) 
        : fileName;
    
    return `${this.baseUrl}/${cleanFileName}`;
  }

  /**
   * Downloads an image as a blob
   * @param fileName The name of the image file
   * @returns Observable<Blob> The image as a blob
   */
  downloadImage(fileName: string): Observable<Blob> {
    const url = this.getImageUrl(fileName);
    return this.http.get(url, {
      responseType: 'blob',
      withCredentials: true
    });
  }

  /**
   * Gets image as base64 string for preview purposes
   * @param fileName The name of the image file
   * @returns Observable<string> Base64 encoded image
   */
  getImageAsBase64(fileName: string): Observable<string> {
    return new Observable<string>(observer => {
      this.downloadImage(fileName).subscribe({
        next: (blob) => {
          const reader = new FileReader();
          reader.onloadend = () => observer.next(reader.result as string);
          reader.onerror = () => observer.error('Failed to read image as base64');
          reader.readAsDataURL(blob);
        },
        error: (error) => observer.error(error)
      });
    });
  }

  /**
   * Uploads multiple images
   * @param files Array of image files to upload
   * @returns Observable<string[]> Array of uploaded image URLs
   */
  uploadMultipleImages(files: File[]): Observable<string[]> {
    return new Observable<string[]>(observer => {
      const results: string[] = [];
      let completed = 0;
      let hasError = false;
      
      if (files.length === 0) {
        observer.next([]);
        observer.complete();
        return;
      }
      
      files.forEach((file, index) => {
        this.uploadImage(file).subscribe({
          next: (imageUrl) => {
            results[index] = imageUrl;
            completed++;
            if (completed === files.length && !hasError) {
              observer.next(results);
              observer.complete();
            }
          },
          error: (error) => {
            if (!hasError) {
              hasError = true;
              observer.error(error);
            }
          }
        });
      });
    });
  }

  /**
   * Validates if the file is a valid image
   * @param file The file to validate
   * @returns boolean true if valid image file
   */
  isValidImageFile(file: File): boolean {
    const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp'];
    return validTypes.includes(file.type);
  }

  /**
   * Gets the file extension from filename
   * @param fileName The filename
   * @returns string File extension
   */
  getFileExtension(fileName: string): string {
    return fileName.split('.').pop()?.toLowerCase() || '';
  }

  /**
   * Formats file size to human readable format
   * @param bytes File size in bytes
   * @returns string Formatted file size
   */
  formatFileSize(bytes: number): string {
    if (bytes === 0) return '0 Bytes';
    
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
  }

  /**
   * Creates a thumbnail URL (if your backend supports it)
   * @param fileName The original filename
   * @param size Thumbnail size (e.g., 'small', 'medium', 'large')
   * @returns string Thumbnail URL
   */
  getThumbnailUrl(fileName: string, size: 'small' | 'medium' | 'large' = 'medium'): string {
    // This assumes your backend might support thumbnail generation
    // Modify according to your backend implementation
    const cleanFileName = fileName.startsWith('/images/') ? fileName.substring(8) : fileName;
    return `${this.baseUrl}/thumbnail/${size}/${cleanFileName}`;
  }

  /**
   * Checks if an image exists on the server
   * @param fileName The name of the image file
   * @returns Observable<boolean> true if image exists
   */
  imageExists(fileName: string): Observable<boolean> {
    return this.downloadImage(fileName).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }

  /**
   * Validates file size
   * @param file The file to validate
   * @param maxSizeInMB Maximum allowed size in MB
   * @returns boolean true if file size is valid
   */
  isValidFileSize(file: File, maxSizeInMB: number = 5): boolean {
    const maxSizeInBytes = maxSizeInMB * 1024 * 1024;
    return file.size <= maxSizeInBytes;
  }

  /**
   * Gets image dimensions
   * @param file The image file
   * @returns Promise<{width: number, height: number}> Image dimensions
   */
  getImageDimensions(file: File): Promise<{width: number, height: number}> {
    return new Promise((resolve, reject) => {
      const img = new Image();
      const url = URL.createObjectURL(file);
      
      img.onload = () => {
        URL.revokeObjectURL(url);
        resolve({ width: img.width, height: img.height });
      };
      
      img.onerror = () => {
        URL.revokeObjectURL(url);
        reject(new Error('Failed to load image'));
      };
      
      img.src = url;
    });
  }

  /**
   * Compresses an image file
   * @param file The image file to compress
   * @param quality Compression quality (0-1)
   * @param maxWidth Maximum width
   * @param maxHeight Maximum height
   * @returns Promise<File> Compressed image file
   */
  compressImage(file: File, quality: number = 0.8, maxWidth: number = 1920, maxHeight: number = 1080): Promise<File> {
    return new Promise((resolve, reject) => {
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d');
      const img = new Image();
      
      img.onload = () => {
        // Calculate new dimensions
        let { width, height } = img;
        
        if (width > maxWidth) {
          height = (height * maxWidth) / width;
          width = maxWidth;
        }
        
        if (height > maxHeight) {
          width = (width * maxHeight) / height;
          height = maxHeight;
        }
        
        canvas.width = width;
        canvas.height = height;
        
        // Draw and compress
        ctx?.drawImage(img, 0, 0, width, height);
        
        canvas.toBlob((blob) => {
          if (blob) {
            const compressedFile = new File([blob], file.name, {
              type: file.type,
              lastModified: Date.now()
            });
            resolve(compressedFile);
          } else {
            reject(new Error('Failed to compress image'));
          }
        }, file.type, quality);
      };
      
      img.onerror = () => reject(new Error('Failed to load image for compression'));
      img.src = URL.createObjectURL(file);
    });
  }
}